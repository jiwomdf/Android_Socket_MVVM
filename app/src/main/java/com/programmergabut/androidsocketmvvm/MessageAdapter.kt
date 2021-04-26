package com.programmergabut.androidsocketmvvm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.androidsocketmvvm.databinding.ItemReceivedMessageBinding
import com.programmergabut.androidsocketmvvm.databinding.ItemReceivedPhotoBinding
import com.programmergabut.androidsocketmvvm.databinding.ItemSentImageBinding
import com.programmergabut.androidsocketmvvm.databinding.ItemSentMessageBinding
import org.json.JSONObject

class MessageAdapter(private val inflater: LayoutInflater): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "MessageAdapter"

    companion object {
        private const val TYPE_MESSAGE_SENT = 0
        private const val TYPE_MESSAGE_RECEIVED = 1
        private const val TYPE_IMAGE_SENT = 2
        private const val TYPE_IMAGE_RECEIVED = 3
    }

    private val messages = arrayListOf<JSONObject>()

    fun addItem(message: JSONObject){
        messages.add(message)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_MESSAGE_SENT -> {
                SentMessageHolder(ItemSentMessageBinding.inflate(inflater))
            }
            TYPE_MESSAGE_RECEIVED -> {
                ReceiveMessageHolder(ItemReceivedMessageBinding.inflate(inflater))
            }
            TYPE_IMAGE_SENT -> {
                SentImageHolder(ItemSentImageBinding.inflate(inflater))
            }
            TYPE_IMAGE_RECEIVED -> {
                ReceiveImageHolder(ItemReceivedPhotoBinding.inflate(inflater))
            }
            else -> {
                SentMessageHolder(ItemSentMessageBinding.inflate(inflater))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if(message.getBoolean(ChatActivity.IS_SENT)){
            if(message.has(ChatActivity.MESSAGE)){
                (holder as SentMessageHolder).bind(message)
            } else {
                val bitmap = getBitmapFromString(message.getString("image"))
                Log.d(TAG, bitmap.toString())
            }
        } else {
            if(message.has(ChatActivity.MESSAGE)){
                (holder as ReceiveMessageHolder).bind(message)
            } else {
                val bitmap = getBitmapFromString(message.getString("image"))
                Log.d(TAG, bitmap.toString())
            }
        }
    }

    private fun getBitmapFromString(image: String): Bitmap? {
        val bytes = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if(message.getBoolean(ChatActivity.IS_SENT)){
            if(message.has(ChatActivity.MESSAGE))
                TYPE_MESSAGE_SENT
            else
                TYPE_IMAGE_SENT
        } else {
            if(message.has(ChatActivity.MESSAGE))
                TYPE_MESSAGE_RECEIVED
            else
                TYPE_IMAGE_RECEIVED
        }
    }


    private inner class SentMessageHolder(private val binding: ItemSentMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: JSONObject){
            binding.tvSend.text = message.getString(ChatActivity.MESSAGE)
        }
    }

    private inner class SentImageHolder(private val binding: ItemSentImageBinding) : RecyclerView.ViewHolder(binding.root){

    }

    private inner class ReceiveMessageHolder(private val binding: ItemReceivedMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: JSONObject){
            binding.tvReceived.text = message.getString(ChatActivity.MESSAGE)
        }
    }

    private inner class ReceiveImageHolder(private val binding: ItemReceivedPhotoBinding) : RecyclerView.ViewHolder(binding.root) {

    }

}