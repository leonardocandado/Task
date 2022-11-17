package com.devmasterteam.tasks.service.repository

import android.content.Context
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.TaskModel
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient
import com.devmasterteam.tasks.service.repository.remote.TaskService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskRepository(context: Context) : BaseRepository(context) {

    private val remote = RetrofitClient.getService(TaskService::class.java)

    fun create(task: TaskModel, listener: APIListener<Boolean>) {
        if (!isConnection()) {
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }
        executeCall(
            remote.create(
                task.priorityId,
                task.description,
                task.dueDate,
                task.complete
            ),
            listener
        )
    }

    fun update(task: TaskModel, listener: APIListener<Boolean>) {
        if (!isConnection()) {
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }
        executeCall(
            remote.update(
                task.id,
                task.priorityId,
                task.description,
                task.dueDate,
                task.complete
            ),
            listener
        )
    }

    fun list(listener: APIListener<List<TaskModel>>) {

        if (!isConnection()) {
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        executeCall(remote.list(), listener)
    }

    fun listNext(listener: APIListener<List<TaskModel>>) {
        if (!isConnection()) {
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }
        executeCall(remote.listNext(), listener)
    }

    fun listOverdue(listener: APIListener<List<TaskModel>>) {
        if (!isConnection()) {
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }
        executeCall(remote.listOverdue(), listener)
    }

    fun delete(id: Int, listener: APIListener<Boolean>) {
        if (!isConnection()) {
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }
        executeCall(remote.delete(id), listener)
    }

    fun complete(id: Int, listener: APIListener<Boolean>) {
        if (!isConnection()) {
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }
        executeCall(remote.complete(id), listener)
    }

    fun undo(id: Int, listener: APIListener<Boolean>) {
        if(!isConnection()){
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }
        executeCall(remote.undo(id), listener)
    }

    fun load(id: Int, listener: APIListener<TaskModel>) {
        if(!isConnection()){
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }
        executeCall(remote.load(id), listener)
    }

}