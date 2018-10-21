package com.github.satoshun.example.androidx.room

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.github.satoshun.example.common.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RoomMainActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val database = Room
        .databaseBuilder(this, MyDatabase::class.java, "database")
        .build()

    val adapter = MainAdapter(MainItemCallback())
    with(recycler) {
      this.adapter = adapter
      this.layoutManager = LinearLayoutManager(this@RoomMainActivity)
    }
    val authors = LivePagedListBuilder(database.author().getAuthors(), 10)
        .build()
    authors.observe(this, Observer {
      adapter.submitList(it!!)
    })

    var i = 0
    launch(Dispatchers.IO) {
      while (true) {
        delay(3000)
        val author = Author(name = "Mr ${i++}")
        author.id = database.author().insert(author)
      }
    }
  }
}

private class MainAdapter(
  diffCallback: DiffUtil.ItemCallback<Author>
) : PagedListAdapter<Author, MainViewHolder>(diffCallback) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
    return MainViewHolder(TextView(parent.context))
  }

  override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
    val user = getItem(position)!!
    holder.view.text = user.name
  }
}

private class MainViewHolder(
  val view: TextView
) : RecyclerView.ViewHolder(view)

private class MainItemCallback : DiffUtil.ItemCallback<Author>() {
  override fun areItemsTheSame(oldItem: Author, newItem: Author): Boolean {
    return oldItem == newItem
  }

  override fun areContentsTheSame(oldItem: Author, newItem: Author): Boolean {
    return oldItem == newItem
  }
}
