package picam;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;


/**
 *
 * @author edgmarti
 */
public class CamPi4J {

    public static String PICTUREFILENAME = "picam_";
    static Runtime rt = Runtime.getRuntime();
    static int increment;
    public static String fileName;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            final GpioController gpio = GpioFactory.getInstance();
            final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00,
                    "MyButton", PinPullResistance.PULL_DOWN); //GPIO_00 = 17
            final GpioPinDigitalOutput myLed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04,
                    "My LED", PinState.LOW); //GPIO_04 = 23

            myButton.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                    try {
                        if (event.getState().isHigh()) {
                            myLed.pulse(1000);
                            // display pin state on console
                            System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = "
                                    + event.getState());
                            fileName = "/var/www/" + PICTUREFILENAME + increment++ + ".jpg";
                            Process p = rt.exec("raspistill -t 8000 -o " + fileName);
                            p.waitFor();
                        }
                    } catch (Exception e) {
                        System.out.println("Exception:" + e.getMessage());
                    }
                }
            });

            // keep program running until user aborts (CTRL-C)
            for (;;) {
                Thread.sleep(500);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
