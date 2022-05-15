package com.example.schoolplanner.ui.subjects

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolplanner.DBHelper
import com.example.schoolplanner.Subject_DBInfo
import com.example.schoolplanner.databinding.FragmentSubjectsBinding

class SubjectsFragment : Fragment() {

    private var _binding: FragmentSubjectsBinding? = null
    private val binding get() = _binding!!

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<AddSubjectRecyclerAdapter.ViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubjectsBinding.inflate(inflater, container, false)

        binding.addSubject.setOnClickListener {
            val intent = Intent(activity, AddSubject::class.java)
            activity?.startActivity(intent)
        }
        return binding.root
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = AddSubjectRecyclerAdapter(context, DBHelper(context).writableDatabase)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}