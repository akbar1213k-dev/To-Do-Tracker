package com.example.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    fun getTasksByDate(date: String): Flow<List<Task>> = taskDao.getTasksByDate(date)
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTaskById(id: Int) = taskDao.deleteTaskById(id)
    suspend fun insertTasks(tasks: List<Task>) = taskDao.insertTasks(tasks)
}
