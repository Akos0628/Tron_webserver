package hu.bme.aut.tron;

import hu.bme.aut.tron.data.Direction

public class Bike(val id: String, val name: String, val color: Byte){
    var x: Int = -1
    var y: Int = -1

    private lateinit var controller : Controller


    fun place(x: Int, y: Int){
        this.x = x
        this.y = y
    }

    fun getStep(): Direction {

        return controller.getStep()
    }
}
