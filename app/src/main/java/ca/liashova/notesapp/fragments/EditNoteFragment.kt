package ca.liashova.notesapp.fragments

import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.liashova.notesapp.MainActivity
import ca.liashova.notesapp.R
import ca.liashova.notesapp.databinding.FragmentEditNoteBinding
import ca.liashova.notesapp.model.Note
import ca.liashova.notesapp.viewmodel.NoteViewModel
import java.util.Calendar


class EditNoteFragment : Fragment(R.layout.fragment_edit_note), MenuProvider {

    private var editNoteBinding: FragmentEditNoteBinding? = null
    private val binding get() = editNoteBinding!!
    val currentDateTime = Calendar.getInstance()
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var currentNote: Note

    private val args: EditNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        editNoteBinding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost= requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel
        currentNote = args.note!!

        //Log.d("TAG", "Editing note $currentNote.id ${currentNote.noteBody}")

        binding.editNoteBody.setText(currentNote.noteBody)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.editNoteFab.setOnClickListener{
            saveNote()
        }
        binding.editNoteBody.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun getTitle(body: String): String{
        if(body == "Type here..."){
            return " "
        }else{
            val lines = body.split("\n")
            if (lines.isNotEmpty() && lines[0].length <= 20){
                return lines[0]
            }else if(lines.isNotEmpty() && lines[0].length > 20){
                return lines[0].substring(0, 17) + "..."
            }
        }
        return ""
    }
    private fun deleteNote(){
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Note")
            setMessage("DO you want to delete this note?")
            setPositiveButton("Yes"){_,_ ->
                notesViewModel.deleteNote(currentNote)
                Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            }
            setNegativeButton("No", null)
        }.create().show()
    }

    private fun saveNote(){
        val noteBody = binding.editNoteBody.text.toString().trim()
        val noteTitle = getTitle(noteBody)
        if (noteBody.isNotEmpty()){
            val note = Note(currentNote.id, noteTitle, noteBody, currentDateTime.time.toString())
            notesViewModel.updateNote(note)

            //Toast.makeText(context,"Note edited ${currentDateTime.time}", Toast.LENGTH_SHORT).show()
            view?.findNavController()?.popBackStack(R.id.homeFragment, false)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_node, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.deleteMenu -> {
                deleteNote()
                true
            }
            android.R.id.home -> {
                saveNote()
                //findNavController().navigateUp()
                true
            }else -> false
        }
    }

    override fun onDestroy(){
        super.onDestroy()
        editNoteBinding = null
    }


}