package net.ivanvega.missensoresa

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {
    private val gravity = FloatArray(3)
    private  val linear_acceleration = FloatArray(3)
    private var sensorAcelerometer: Sensor? = null
    private var mSensor: Sensor? = null
    private lateinit var sensorManager: SensorManager
    private var mLight: Sensor? = null
    //val displayMetrics = resources.displayMetrics
    //val width = displayMetrics.widthPixels
    //val height = displayMetrics.heightPixels


    //private lateinit var binding: ActivityMainBinding
    /*
    private var x = 0
    private var y = 0
    private var xVelocity = 10
    private var yVelocity = 10
    private val ballRadius = 100
    */

    val sensorEventListener : SensorEventListener = object : SensorEventListener{
        override fun onSensorChanged(event: SensorEvent?) {
            //TODO("Not yet implemented")
            // In this example, alpha is calculated as t / (t + dT),
            // where t is the low-pass filter's time-constant and
            // dT is the event delivery rate.

            val alpha: Float = 0.8f

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event!!.values[0]
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event!!.values[1]
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event!!.values[2]

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = event.values[0] - gravity[0]
            linear_acceleration[1] = event.values[1] - gravity[1]
            linear_acceleration[2] = event.values[2] - gravity[2]

            Log.d("ACELERE", "x=${linear_acceleration[0]} ; y=${linear_acceleration[1]} ; " +
                    "z=${linear_acceleration[2]}")

        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            //TODO("Not yet implemented")
        }

    }

    lateinit var miViewDibujado: MiViewDibujado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         miViewDibujado = MiViewDibujado(this)

        setContentView(miViewDibujado)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val deviceSensors =   sensorManager.getSensorList(Sensor.TYPE_ALL)

        deviceSensors.forEach {
            Log.i("MisSensores", it.toString())
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            // Success! There's a magnetometer.
            Log.i("MisSensores", "MAGNETOMETRO ENCONTRADO")
        } else {
            // Failure! No magnetometer.
            Log.i("MisSensores", "MAGNETOMETRO NO ENCONTRADO")
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            val gravSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_GRAVITY)
            // Use the version 3 gravity sensor.
            mSensor = gravSensors.firstOrNull { it.vendor.contains("Google LLC") && it.version == 3 }
        }
        if (mSensor == null) {
            // Use the accelerometer.
            mSensor = if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            } else {
                // Sorry, there are no accelerometers on your device.
                // You can't play this game.
                null
            }
        }

        Log.i("MisSensores", mSensor.toString())

        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        sensorAcelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


    }

    override fun onResume() {
        super.onResume()
        mLight?.also { light ->
            sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL)
            /*sensorManager.registerListener(sensorEventListener,
                light, SensorManager.SENSOR_DELAY_NORMAL)*/
        }
        sensorAcelerometer?.also {
            sensorManager.registerListener(miViewDibujado,it,
                SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        sensorManager.unregisterListener(miViewDibujado)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        //TODO("Not yet implemented")
        val lux =  p0!!.values[0]
        //(findViewById(R.id.txt) as TextView).text = lux.toString()
        Log.i("LUZhay", lux.toString())

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(sensorEventListener)
    }

}

class  MiViewDibujado (ctx: Context) : View(ctx), SensorEventListener {

     var xPos =
        0f
      var xAcceleration:kotlin.Float = 0f
      var xVelocity:kotlin.Float = 0.0f
     var yPos =        0f
      var yAcceleration:kotlin.Float = 0f
      var yVelocity:kotlin.Float = 0.0f

    var pincel = Paint()
    private var gravity = FloatArray(3)
    private  var linear_acceleration = FloatArray(3)

    init {
        pincel.setColor(Color.RED)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Obtener las dimensiones de la pantalla
        val width = canvas!!.width.toFloat()
        val height = canvas!!.height.toFloat()

        if(xPos == 0f && yPos == 0f){
            xPos = width / 2f
            yPos = height / 2f
        }
        else {
            // Verificar si la pelota está en los límites de la pantalla
            if (xPos > width - 50 || xPos < 50) {
                xVelocity = -xVelocity // Cambiar la dirección de la pelota
            }
            if (yPos > height - 50 || yPos < 50) {
                yVelocity = -yVelocity // Cambiar la dirección de la pelota
            }
        }
        // Actualizar la posición de la pelota
        xPos += xVelocity
        yPos += yVelocity
        //canvas!!.drawCircle(xPos, yPos,50.0F, pincel)

        //canvas!!.drawLine(200F, 200F, 500F, 200F, pincel)
        canvas!!.drawCircle(xPos, yPos,50.0F, pincel)
        //canvas!!.drawText("Este es un texto dibujado",400F,400F,pincel)

        invalidate()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //TODO("Not yet implemented")
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        val alpha: Float = 0.96f

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event!!.values[0]
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event!!.values[1]
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event!!.values[2]

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0]   //x
        linear_acceleration[1] = event.values[1] - gravity[1]    //y
        linear_acceleration[2] = event.values[2] - gravity[2]   //z

        Log.d("ACELERE", "x=${linear_acceleration[0]} ; y=${linear_acceleration[1]} ; " +
                "z=${linear_acceleration[2]}")

        moverPelota(linear_acceleration[0], linear_acceleration[1] * -1)

    }

    private fun moverPelota( xOrientation: Float,  yOrientation: Float) {
        //TODO("Not yet implemented")
        xAcceleration = xOrientation;
        yAcceleration = yOrientation;
        updateX();
        updateY();

    }

    fun updateX() {
        xVelocity -= xAcceleration * 0.8f
        xPos += xVelocity
    }

    fun updateY() {
        yVelocity -= yAcceleration * 0.8f
        yPos += yVelocity
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //TODO("Not yet implemented")
    }

}