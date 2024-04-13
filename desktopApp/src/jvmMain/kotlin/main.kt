import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.joelkanyi.focusbloom.utils.Sound
import di.startKoinApp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import navigation.SharedNavigatedApp
import java.awt.AWTException
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GridLayout
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.RenderingHints
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import javax.swing.UIManager
import kotlin.system.exitProcess


fun main() {
    startKoinApp()
    application {
        val state = rememberWindowState(WindowPlacement.Floating)
        val imageLoadingScope = rememberCoroutineScope()

        Window(
            title = "Chirrio Messenger",
            onCloseRequest = ::exitApplication,
            state = state,
        ) {
            SetAppIcon()
            SharedNavigatedApp()

            delay {
                val notificationWindow =
                    NotificationWindow("Calling from Adel", "do you want to answer?") {
                        if (window.isMinimized) {
                            window.isMinimized = false
                            imageLoadingScope.launch {
                                state.placement = WindowPlacement.Maximized
                            }
                        } else {
                            state.placement = WindowPlacement.Maximized
                        }
                    }

                notificationWindow.isVisible = true
            }
        }
    }
}

@Composable
fun delay(onClicked: () -> Unit) {
    val imageLoadingScope = rememberCoroutineScope()

    imageLoadingScope.launch {
        delay(3000)
        onClicked()
    }
}

private fun createTrayIcon(onClicked: () -> Unit) {

    val popup = PopupMenu()
    val image = ImageIcon("jetchat_icon.png").image

    val trayIcon = TrayIcon(image)
    trayIcon.addActionListener { onClicked() }

    val tray = SystemTray.getSystemTray()
    val openItem = MenuItem("open")
    openItem.addActionListener(
        ShowMessageListener(
            trayIcon,
            "Holy Fuck",
            "Warning",
            TrayIcon.MessageType.WARNING
        ) {
            onClicked()
        })
    val exitItem = MenuItem("close")

    exitItem.addActionListener {
        tray.remove(trayIcon)
        exitProcess(0)
    }
    popup.add(openItem)
    popup.addSeparator()
    popup.add(exitItem)

    trayIcon.popupMenu = popup

    try {
        tray.add(trayIcon)
        trayIcon.addActionListener(object : ActionListener {
            override fun actionPerformed(p0: ActionEvent?) {
                p0?.actionCommand.toString()

                trayIcon.displayMessage(
                    "Calling From Adel",
                    "You Want to Fuck?",
                    TrayIcon.MessageType.INFO
                )
            }
        })
        trayIcon.displayMessage(
            "Calling From Adel",
            "You Want to Answer?",
            TrayIcon.MessageType.INFO
        )
        Sound.playSound()
    } catch (e: AWTException) {
    }
}

private class ShowMessageListener internal constructor(
    private val trayIcon: TrayIcon,
    private val title: String,
    private val message: String,
    private val messageType: TrayIcon.MessageType,
    onClicked: () -> Unit
) :
    ActionListener {
    override fun actionPerformed(e: ActionEvent) {
        trayIcon.addActionListener { println("Message Clicked") }
        trayIcon.displayMessage(title, message, messageType)
    }
}

class NotificationWindow(title: String?, description: String?, onClicked: () -> Unit) :
    JFrame() {
    init {
        setTitle("Incoming Call")
        setSize(300, 150)
        defaultCloseOperation = DISPOSE_ON_CLOSE
        isUndecorated = true // Remove window decorations


        // Create a rounded border panel with padding

        // Create a rounded border panel with padding
        val panel: JPanel = object : JPanel(BorderLayout()) {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val radius = 10
                val width = width
                val height = height
                val graphics = g as Graphics2D
                graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                )
                graphics.color = background
                graphics.fillRoundRect(0, 0, width - 1, height - 1, radius, radius)
                graphics.color = foreground
                graphics.drawRoundRect(0, 0, width - 1, height - 1, radius, radius)
            }
        }
        panel.background = Color(83, 185, 86) // Set background color

        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10) // Add padding


        val titleLabel = JLabel(title)
        titleLabel.horizontalAlignment = SwingConstants.CENTER
        val descriptionLabel = JLabel(description)
        descriptionLabel.horizontalAlignment = SwingConstants.CENTER

        val acceptButton: JButton = object : JButton("Accept") {
            override fun paintComponent(g: Graphics) {
                if (getModel().isPressed) {
                    g.color = Color(65, 158, 68) // Background color when pressed
                } else {
                    g.color = Color(83, 185, 86) // Background color
                }
                g.fillRoundRect(0, 0, width, height, 10, 10) // Rounded corners
                super.paintComponent(g)
            }
        }
        acceptButton.addActionListener {
            onClicked()
            dispose()
        }
        acceptButton.isFocusPainted = false // Remove focus border

        acceptButton.background = Color(83, 185, 86)
//        acceptButton.foreground = Color.WHITE // Text color

        acceptButton.preferredSize = Dimension(100, 40) // Button size


        val declineButton: JButton = object : JButton("Decline") {
            override fun paintComponent(g: Graphics) {
                if (getModel().isPressed) {
                    g.color = Color(214, 69, 65) // Background color when pressed
                } else {
                    g.color = Color(232, 74, 71) // Background color
                }
                g.fillRoundRect(0, 0, width, height, 10, 10) // Rounded corners
                super.paintComponent(g)
            }
        }
        declineButton.isFocusPainted = false // Remove focus border
        declineButton.background = Color(232, 74, 71)
//        declineButton.foreground = Color.WHITE // Text color

        declineButton.preferredSize = Dimension(100, 40) // Button size

        declineButton.addActionListener {
            dispose()
        }

        val buttonPanel = JPanel(GridLayout(1, 2, 10, 0))

        buttonPanel.background = Color(83, 185, 86)
        buttonPanel.add(acceptButton)
        buttonPanel.add(declineButton)

        panel.add(titleLabel, BorderLayout.NORTH)
        panel.add(descriptionLabel, BorderLayout.CENTER)
        panel.add(buttonPanel, BorderLayout.SOUTH)

        contentPane.add(panel)

        val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
        val x = screenSize.width - (width + 16)
        val y = screenSize.height - (height + 66)

        setLocation(x, y)
        isAlwaysOnTop = true
        Sound.playSound()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SwingUtilities.invokeLater { // Set Nimbus look and feel for modern UI
                try {
                    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel")
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                // Instantiate and display the NotificationWindow
                val notificationWindow =
                    NotificationWindow("Calling from John Doe", "Do you want to answer the call?") {

                    }
                notificationWindow.isVisible = true
            }
        }
    }
}

@Composable
fun FrameWindowScope.SetAppIcon() {
    val density = LocalDensity.current
    val iconPainter = BitmapPainter(
        useResource(
            resourcePath = "jetchat_icon.png",
            block = ::loadImageBitmap
        )
    )
    SideEffect {
        window.iconImage = iconPainter.toAwtImage(
            density = density,
            layoutDirection = LayoutDirection.Ltr,
            size = Size(128f, 128f)
        )
    }
}