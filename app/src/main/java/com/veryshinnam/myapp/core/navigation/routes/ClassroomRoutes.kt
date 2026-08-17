package com.veryshinnam.myapp.core.navigation.routes

object ClassroomRoutes {
    const val CLASSROOM_HOME = "classroom_home"
    const val CREATE_CLASSROOM = "create_classroom"
    const val JOIN_CLASSROOM = "join_classroom"
    const val CLASSROOM_DETAIL = "classroom_detail/{classroomId}"
    const val ASSIGNMENT_LIST = "assignment_list/{classroomId}/{isTeacher}"
    const val CLASSROOM_STORIES = "classroom_stories/{classroomId}/{week}"

    fun classroomDetail(classroomId: Long) = "classroom_detail/$classroomId"
    fun assignmentList(classroomId: Long, isTeacher: Boolean) = "assignment_list/$classroomId/$isTeacher"
    fun classroomStories(classroomId: Long, week: Int) = "classroom_stories/$classroomId/$week"
}
