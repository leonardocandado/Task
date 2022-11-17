package com.devmasterteam.tasks.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.databinding.ActivityTaskFormBinding
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.model.TaskModel
import com.devmasterteam.tasks.viewmodel.TaskFormViewModel
import java.text.SimpleDateFormat
import java.util.*

class TaskFormActivity : AppCompatActivity(), View.OnClickListener,
    DatePickerDialog.OnDateSetListener {

    private lateinit var viewModel: TaskFormViewModel
    private lateinit var binding: ActivityTaskFormBinding
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    private var listPriority: List<PriorityModel> = mutableListOf()
    private var taskID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Variáveis da classe
        viewModel = ViewModelProvider(this).get(TaskFormViewModel::class.java)
        binding = ActivityTaskFormBinding.inflate(layoutInflater)

        // Eventos
        binding.buttonSave.setOnClickListener(this)
        binding.buttonDate.setOnClickListener(this)

        viewModel.loadPriorities()

        loadData()

        observe()

        // Layout
        setContentView(binding.root)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.button_date) {
            handleDate()
        } else if (v.id == R.id.button_save) {
            handleSave()
        }
    }

    override fun onDateSet(v: DatePicker, year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        binding.buttonDate.text = dateFormat.format(calendar.time)
    }

    private fun observe() {
        viewModel.priorityList.observe(this) {
            listPriority = it
            val list = mutableListOf<String>()
            for (priority in it) {
                list.add(priority.description)
            }

            binding.spinnerPriority.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                list
            )
        }

        viewModel.taskSave.observe(this) {
            if (it.status()) {
                if(taskID == 0) {
                    toast("Tarefa criada com sucesso!")
                } else {
                    toast("Atualização realizada com sucesso!")
                }
                finish()
            } else {
                toast(it.message())
            }
        }

        viewModel.task.observe(this) {
            binding.buttonSave.text = "Atualizar tarefa"
            binding.editDescription.setText(it.description)
            binding.checkComplete.isChecked = it.complete
            binding.spinnerPriority.setSelection(getIndex(it.priorityId))

            val date = SimpleDateFormat("yyyy-MM-dd").parse(it.dueDate)
            binding.buttonDate.text = SimpleDateFormat("dd/MM/yyyy").format(date!!)
        }

        viewModel.taskLoad.observe(this) {
            if (!it.status()) {
                toast(it.message())
                finish()
            }
        }

    }
    private fun getIndex(priorityId: Int): Int{
        var index = 0
        for(l in listPriority){
            if(l.id == priorityId) {break}
            index++
        }
        return index
    }

    private fun handleDate() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this, this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun handleSave() {
        val task = TaskModel().apply {
            this.id = taskID
            this.description = binding.editDescription.text.toString()
            this.complete = binding.checkComplete.isChecked
            this.dueDate = binding.buttonDate.text.toString()
            val index = binding.spinnerPriority.selectedItemPosition
            this.priorityId = listPriority[index].id
        }
        viewModel.save(task)
    }

    private fun toast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_LONG).show()
    }

    private fun loadData() {
        val bundle = intent.extras
        if (bundle != null) {
            taskID = bundle.getInt(TaskConstants.BUNDLE.TASKID)
            viewModel.load(taskID)

        }
    }


}