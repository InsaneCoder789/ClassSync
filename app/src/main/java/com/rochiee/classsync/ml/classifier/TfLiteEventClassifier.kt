package com.rochiee.classsync.ml.classifier

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Locale

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
                val inputArray = arrayOf(session.tokenizer.tokenize(inputText))
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
            val tokenizer = loadTokenizer()
            val modelBuffer = loadModelBuffer()
            if (labels.isEmpty() || tokenizer == null || modelBuffer == null) {
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
                labels = labels,
                tokenizer = tokenizer
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

    private fun loadTokenizer(): EventTextTokenizer? {
        return runCatching {
            val tokenToIndex = linkedMapOf<String, Int>()
            appContext.assets.open(VOCAB_ASSET_PATH).bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val separatorIndex = line.indexOf('\t')
                    if (separatorIndex <= 0) return@forEach
                    val index = line.substring(0, separatorIndex).toIntOrNull() ?: return@forEach
                    val token = line.substring(separatorIndex + 1)
                    tokenToIndex[token] = index
                }
            }
            EventTextTokenizer(tokenToIndex = tokenToIndex)
        }.getOrNull()
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
        private const val VOCAB_ASSET_PATH = "classsync_event_vocabulary.tsv"
    }

    private data class ClassifierSession(
        val interpreter: Interpreter,
        val labels: List<String>,
        val tokenizer: EventTextTokenizer
    )

    private class EventTextTokenizer(
        private val tokenToIndex: Map<String, Int>
    ) {
        fun tokenize(text: String): IntArray {
            val tokens = normalize(text)
                .split(Regex("\\s+"))
                .filter { it.isNotBlank() }
                .take(SEQUENCE_LENGTH)

            return IntArray(SEQUENCE_LENGTH).apply {
                tokens.forEachIndexed { index, token ->
                    this[index] = tokenToIndex[token] ?: UNKNOWN_TOKEN_INDEX
                }
            }
        }

        private fun normalize(text: String): String {
            return text
                .lowercase(Locale.getDefault())
                .replace(PUNCTUATION_REGEX, " ")
                .replace(Regex("\\s+"), " ")
                .trim()
        }

        companion object {
            private val PUNCTUATION_REGEX = Regex("[!\"#$%&()*,\\-./:;<=>?@\\[\\\\\\]^_`{|}~']")
            private const val SEQUENCE_LENGTH = 120
            private const val UNKNOWN_TOKEN_INDEX = 1
        }
    }
}
