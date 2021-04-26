package com.programmergabut.androidsocketmvvm

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.programmergabut.androidsocketmvvm.databinding.ActivityChatBinding
import com.programmergabut.androidsocketmvvm.databinding.ActivityMainBinding
import okhttp3.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class ChatActivity : AppCompatActivity(), TextWatcher {

    companion object {
        const val NAME = "name"
        const val IS_SENT = "isSent"
        const val MESSAGE = "message"
    }

    private lateinit var binding: ActivityChatBinding
    private val SERVER_PATH = "ws://echo.websocket.org"

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var name: String
    private lateinit var webSocket: WebSocket
    private val IMAGE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        name = intent.extras?.getString(NAME) ?: ""
        initiateSocketConnection()
    }

    private fun initiateSocketConnection() {
        val client = OkHttpClient()
        val request = Request.Builder().url(SERVER_PATH).build()
        webSocket = client.newWebSocket(request, SocketListener())
    }

    private inner class SocketListener: WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)

            runOnUiThread {
                val jsonObject = JSONObject(text)
                jsonObject.put(IS_SENT, false)

                messageAdapter.addItem(jsonObject)

                binding.rvMain.smoothScrollToPosition(messageAdapter.itemCount - 1)
            }
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)

            runOnUiThread {
                Toast.makeText(this@ChatActivity, "Socket Connection Successful", Toast.LENGTH_SHORT).show()
                initializeView()
            }
        }
    }

    private fun initializeView() {

        messageAdapter = MessageAdapter(layoutInflater)
        binding.rvMain.adapter = messageAdapter
        binding.rvMain.layoutManager = LinearLayoutManager(this)

        binding.etMsg.addTextChangedListener(this)
        binding.btnSend.setOnClickListener {
            val jsonObject = JSONObject()

            jsonObject.put(NAME, name)
            jsonObject.put(MESSAGE, binding.etMsg.text.toString())

            webSocket.send(jsonObject.toString())
            jsonObject.put(IS_SENT, true)
            messageAdapter.addItem(jsonObject)
            binding.rvMain.smoothScrollToPosition(messageAdapter.itemCount - 1)
            resetMessageEdit()
        }

        binding.ivPickImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), IMAGE_REQUEST_CODE)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {

        val string = s.toString().trim()
        if(string.isEmpty()){
            resetMessageEdit()
        } else {
            binding.btnSend.visibility = View.VISIBLE
            binding.ivPickImg.visibility = View.INVISIBLE
        }
    }

    private fun resetMessageEdit() {
        binding.etMsg.removeTextChangedListener(this)
        binding.etMsg.setText("")

        binding.btnSend.visibility = View.INVISIBLE
        binding.ivPickImg.visibility = View.VISIBLE
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMAGE_REQUEST_CODE && requestCode == RESULT_OK){
            if(data?.data == null) return
            val `is` = contentResolver.openInputStream(data.data!!)
            val bitmap = BitmapFactory.decodeStream(`is`) ?: return

            sendImage(bitmap)

        }
    }

    private fun sendImage(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)

        val jsonObject = JSONObject()
        jsonObject.put(NAME,  name)
        jsonObject.put("image", base64)

        webSocket.send(jsonObject.toString())

        jsonObject.put(IS_SENT, true)

        messageAdapter.addItem(jsonObject)
        binding.rvMain.smoothScrollToPosition(messageAdapter.itemCount - 1)
    }
}