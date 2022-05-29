package com.life.planner.ui.notes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.life.planner.databinding.FragmentNotesBinding

/**
 * Klasa obsługująca widok fragmentu Notes
 *
 */
class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    /**
     * Funkcja wykonywana przy tworzeniu widoku
     *
     * @param inflater - uchwyt LayoutInflater
     * @param container - uchwyt grupy widoków
     * @param savedInstanceState - uchwyt Bundle
     * @return widok fragmentu
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        binding.addNotesMenu.setOnClickListener {
            val intent = Intent(requireActivity(), AddNote::class.java)
            requireActivity().startActivity(intent)
        }
        return binding.root
    }

    /**
     * Funkcja wykonywana przy stworzeniu widoku
     *
     * @param itemView - uchwyt widoku
     * @param savedInstanceState - uchwyt Bundle
     */
    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = NotesRecyclerAdapter(NotesDBHelper(context).writableDatabase)
        }
    }

    /**
     * Funkcja wykonywana przy wznowieniu aplikacji
     *
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.notesRecyclerView.adapter?.notifyDataSetChanged()
    }

    /**
     * Funkcja wykonywana przy niszczeniu widoku
     *
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}