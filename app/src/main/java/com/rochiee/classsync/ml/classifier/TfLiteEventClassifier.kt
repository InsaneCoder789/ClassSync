package com.rochiee.classsync.ml.classifier

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TfLiteEventClassifier(
    context: Context
) {
    private val appContext = context.applicationContext
    private val interpreterLock = Any()
    @Volatile
    private var cachedSession: ClassifierSession? = null
    @Volatile
    private var attemptedSessionLoad: Boolean = false

    fun classify(
        input: EventClassifierInput,
        createTasksFromActionableNoDateAnnouncements: Boolean
    ): EventClassificationResult? {
        val inputText = input.buildText()
        if (inputText.isBlank()) {
            return null
        }

        val session = getOrCreateSession() ?: return null

        return runCatching {
            synchronized(interpreterLock) {
                val outputShape = session.interpreter.getOutputTensor(0).shape()
                if (outputShape.isEmpty()) return null

                val outputSize = outputShape.last()
                if (outputSize <= 0 || outputSize != session.labels.size) {
                    return null
                }

                val output = Array(1) { FloatArray(outputSize) }
                val inputArray = arrayOf(arrayOf(inputText))
                session.interpreter.run(inputArray, output)

                val probabilities = output.firstOrNull() ?: return null
                val bestIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: return null
                val confidence = probabilities[bestIndex]
                val label = session.labels.getOrNull(bestIndex)?.let(EventClassificationLabel::fromRaw) ?: return null

                ClassificationMapper.toResult(
                    label = label,
                    source = ClassificationSource.TFLITE,
                    confidence = confidence,
                    reason = "TFLite classifier predicted $label with confidence $confidence.",
                    inputText = inputText,
                    dueDateMillis = input.dueDateMillis,
                    createTasksFromActionableNoDateAnnouncements = createTasksFromActionableNoDateAnnouncements
                )
            }
        }.getOrNull()
    }

    private fun getOrCreateSession(): ClassifierSession? {
        cachedSession?.let { return it }
        if (attemptedSessionLoad) return null

        synchronized(interpreterLock) {
            cachedSession?.let { return it }
            if (attemptedSessionLoad) return null
            attemptedSessionLoad = true

            val labels = loadLabels()
            val modelBuffer = loadModelBuffer()
            if (labels.isEmpty() || modelBuffer == null) {
                return null
            }

            val interpreter = runCatching {
                Interpreter(
                    modelBuffer,
                    Interpreter.Options().apply {
                        setNumThreads(2)
                    }
                )
            }.getOrNull() ?: return null

            return ClassifierSession(
                interpreter = interpreter,
                labels = labels
            ).also { cachedSession = it }
        }
    }

    private fun loadLabels(): List<String> {
        return runCatching {
            appContext.assets.open(LABELS_ASSET_PATH).bufferedReader().useLines { lines ->
                lines.map(String::trim).filter(String::isNotBlank).toList()
            }
        }.getOrDefault(emptyList())
    }

    private fun loadModelBuffer(): ByteBuffer? {
        return loadMappedModelBuffer() ?: loadCopiedModelBuffer()
    }

    private fun loadMappedModelBuffer(): ByteBuffer? {
        return runCatching {
            appContext.assets.openFd(MODEL_ASSET_PATH).use { fileDescriptor ->
                FileInputStream(fileDescriptor.fileDescriptor).channel.use { channel ->
                    channel.map(
                        java.nio.channels.FileChannel.MapMode.READ_ONLY,
                        fileDescriptor.startOffset,
                        fileDescriptor.declaredLength
                    )
                }
            }
        }.getOrNull()
    }

    private fun loadCopiedModelBuffer(): ByteBuffer? {
        return runCatching {
            val bytes = appContext.assets.open(MODEL_ASSET_PATH).use { it.readBytes() }
            ByteBuffer.allocateDirect(bytes.size)
                .order(ByteOrder.nativeOrder())
                .apply {
                    put(bytes)
                    rewind()
                }
        }.getOrNull()
    }

    companion object {
        private const val MODEL_ASSET_PATH = "classsync_event_classifier.tflite"
        private const val LABELS_ASSET_PATH = "classsync_event_labels.txt"
    }

    private data class ClassifierSession(
        val interpreter: Interpreter,
        val labels: List<String>
    )
}
