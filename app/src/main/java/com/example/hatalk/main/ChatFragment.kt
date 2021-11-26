package com.example.hatalk.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.hatalk.R
import com.example.hatalk.databinding.FragmentChatBinding
import com.example.hatalk.main.chat.ChatingActivity
import com.example.hatalk.main.data.Friends
import com.example.hatalk.main.data.Partner
import com.example.hatalk.main.userModel.UserModel
import com.example.hatalk.network.UserApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
    private val TAG = "HEART"
    private val sharedViewModel: UserModel by activityViewModels()
    private var binding: FragmentChatBinding? = null

    companion object{
        fun newInstance() : ChatFragment {
            return ChatFragment()
        }
    }

    private var friends : ArrayList<Partner> = arrayListOf()

    @SuppressLint("UserRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentChatBinding.inflate(inflater, container, false)
        binding = fragmentBinding

            // do something after 1000ms
        sharedViewModel.friends?.forEach {
            friends.add(it.partner)
        }
        val recyclerView = binding?.chatRecycler
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.adapter = RecyclerViewAdapter()


        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_list, parent, false))
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.chat_item_image)
            val textView : TextView = itemView.findViewById(R.id.chat_item_text)
            val textViewEmail : TextView = itemView.findViewById(R.id.chat_item_email)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            Glide.with(holder.itemView.context).load(friends[position].photoUrl)
                .apply(RequestOptions().circleCrop())
                .into(holder.imageView)
            holder.textView.text = friends[position].nickname
            holder.textViewEmail.text = friends[position].id

            holder.itemView.setOnClickListener{
                val intent = Intent(context, ChatingActivity::class.java)
                intent.putExtra("destinationUid", friends[position].id)
                context?.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return friends.size
        }
    }

}