package ru.focusstart.kireev.homeworkrxjava.fragments

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*
import ru.focusstart.kireev.homeworkrxjava.util.MyTextWatcher
import ru.focusstart.kireev.homeworkrxjava.R
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

class MainFragment : Fragment(R.layout.fragment_main) {

    private var story = ""
    private lateinit var disposable: Disposable

    companion object {
        private const val BOOK_ID = R.raw.book
        fun newInstance() = MainFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadStory()

        //первая часть задания - просто вывести количество совпадений
//        disposable = getDisposableFirstTask()

        //вторая часть задания - выводит каждое найденное количество совпадений
        disposable = getDisposableSecondTask()
    }

    private fun getDisposableFirstTask() =
        createFlowable()
            .subscribeOn(Schedulers.io())
            .debounce(700, TimeUnit.MILLISECONDS)
            .map { searchText ->
                return@map if (searchText.isEmpty()) {
                    0
                } else {
                    story.split(searchText).size - 1
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                text_view_results.text = result.toString()
            }, {throwable ->
                Log.e("mytag", throwable.message, throwable)
            })

    private fun getDisposableSecondTask() =
        createFlowable()
            .subscribeOn(Schedulers.io())
            .debounce(700, TimeUnit.MILLISECONDS)
            .flatMap { searchText ->
                checkMatches(searchText)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                text_view_results.text = result.toString()
            }, {throwable ->
                Log.e("mytag", throwable.message, throwable)
            })

    //создает поток количества совпадений из строки с текстом
    private fun checkMatches(text: String) =
        Flowable.create<Int>({ emitter ->
            var count = 0
            if (text.isEmpty()) {
                emitter.onNext(count)
            } else {
                //разбивает текст на слова по пробелу
                story.split(" ").forEach {
                    count += text.toRegex().findAll(it).count()
                    emitter.onNext(count)
                }
            }
            emitter.onComplete()
        }, BackpressureStrategy.BUFFER)

    //создает поток данных из edit text
    private fun createFlowable() =
        Flowable.create<String>({ emitter ->
            val textWatcher = MyTextWatcher { s ->
                if (!emitter.isCancelled) {
                    emitter.onNext(s.toString())
                }
            }
            emitter.setCancellable {
                text_input_search.editText?.removeTextChangedListener(
                    textWatcher
                )
            }
            text_input_search.editText?.addTextChangedListener(textWatcher)
        }, BackpressureStrategy.BUFFER)

    //загрузка книги из файла ресурсов в text view
    private fun loadStory() {
        val inputStream = resources.openRawResource(BOOK_ID)
        try {
            story = convertStreamToString(inputStream)
            inputStream.close()
        } catch (ex: IOException) {
            Log.e("mytag", ex.message, ex)
        }
        text_view_story.movementMethod = ScrollingMovementMethod()
        text_view_story.text = story
    }

    //возвращает строку из input stream
    @Throws(IOException::class)
    private fun convertStreamToString(inputStream: InputStream): String {
        val byteArray = ByteArrayOutputStream()
        var i = inputStream.read()
        while (i != -1) {
            byteArray.write(i)
            i = inputStream.read()
        }
        return byteArray.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
    }
}