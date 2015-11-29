package itmo.escience.simenv.environment.entities

import itmo.escience.simenv.common.NameAndId

/**
 * Created by user on 02.11.2015.
 */
class Workflow(val id: WorkflowId, val name:String, val headTask:HeadTask) extends NameAndId[WorkflowId]
