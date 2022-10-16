package nstv.canvasExtensions.maths

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

const val TotalPercentage = 100.0f
val FullCircleAngleInRadians = Math.toRadians(360.0)

/**
 * Returns a point in a circumference given the angle from the x axis in radians,
 * the circle radius (r) and the circle center (x0, y0)
 *
 * Crazy maths:
 * - x obtained with trigonometry: cos(angle) = Adjacent/Hypotenuse = (x-x0)/radius
 * => x = cos(angle) * r + x0
 *
 * - y obtained with trigonometry: sin(angle) = Opposite/Hypotenuse = (y-y0)/radius
 * => y = sin(angle) * r + y0
 *
 **/
fun getCircumferencePointForAngle(
    angleInRadians: Double,
    radius: Float,
    circleCenter: Offset = Offset.Zero
): Offset {
    val x = cos(angleInRadians).times(radius).plus(circleCenter.x).toFloat()
    val y = sin(angleInRadians).times(radius).plus(circleCenter.y).toFloat()

    return Offset(x, y)
}

/**
 * Returns the angle from the x axis in radians for a given position.
 * The angle is always positive and comes from the polar coordinates system.
 *
 **/
internal fun getCircumferenceAngleInRadiansForPoint(
    point: Offset,
    center: Offset = Offset.Zero,
): Float {
    var angle = atan2(point.y - center.y, point.x - center.x)
    if (angle < 0) {
        // atan2 returns values in the range [-PI, PI]
        // We want values in the range [0, 2PI]
        angle += (2 * Math.PI).toFloat()
    }
    return angle
}

/**
 * Objective:
 * Get the control point for the Quadratic Bezier Curve.
 * The control point is perpendicular to the line between p1&p2, passing through the middle of it
 * and at half the distance between p1&p2
 *
 * Strategy:
 * Get the formula of the line between p1&p2 (L1)
 * Get the perpendicular line (L2) of L1, we only need the slope
 * Use the perpendicular line (L2) slope to get the angle of L2 from the x axis
 * A circle can be formed from the center of L1 with a radius of half the distance of p1&p2
 * Use this circle and the angle of L2 to get the control point at half the distance of L1, from the
 * middle point of L1, using getCircumferencePointForAngle()
 *
 * Using:
 * - line formula: y = mx + b where m is the slope and b is the yIntercept
 * - perpendicular line formula slope is the negative reciprocal: m1 * m2 = -1 => m2 = -1/m1
 * - slope relation to angle: m = (y-y0)/(x-x0) = tan(angle) => angle = tan^-1(m)
 *
 */
fun getCurveControlPoint(
    p1: Offset,
    p2: Offset,
    center: Offset,
): Offset {

    if (p1 == p2) {
        return p1
    }

    // 1. get initial line formula slope
    val m1 = p1.getSlopeTo(p2)

    // 2. get perpendicular line slope: m1*m2 = -1
    val m2 = -1 / m1

    // 3. get middle point
    val middlePoint = getMiddlePoint(p1, p2)

    val radius = p2.distanceToOffset(p1).div(2)

    val angle = atan(m2).toDouble()

    /**
     * The angle obtained starts in the x axis up, using it we get a fluff opening to the right,
     * but the perpendicular line cuts the helper circle in 2 spaces: in angle and in angle + 180°.
     * We obtain both points and choose the one that is farthest from the center
     * */

    val cp1 = getCircumferencePointForAngle(
        angleInRadians = angle,
        radius = radius,
        circleCenter = middlePoint
    )

    val cp2 = getCircumferencePointForAngle(
        angleInRadians = angle + PI,
        radius = radius,
        circleCenter = middlePoint
    )

    return if (cp1.squareDistanceToOffset(center) > cp2.squareDistanceToOffset(center)) {
        cp1
    } else {
        cp2
    }
}

fun getMiddlePoint(p1: Offset, p2: Offset): Offset {
    val x = (p2.x + p1.x).div(2)
    val y = (p2.y + p1.y).div(2)
    return Offset(x, y)
}

fun getRectTopLeftForDiagonal(lineStart: Offset, lineEnd: Offset) = Offset(
    x = if (lineStart.x < lineEnd.x) lineStart.x else lineEnd.x,
    y = if (lineStart.y < lineEnd.y) lineStart.y else lineEnd.y
)

/**
 * Good ol' Pythagoras h^2 = a^2 + b^2 => distance^2 = (x1 - x0)^2 + (y1 - y0)^2
 */
fun Offset.distanceToOffset(offset: Offset): Float =
    sqrt(squareDistanceToOffset(offset))

fun Offset.squareDistanceToOffset(offset: Offset): Float =
    (offset.x - this.x).pow(2) + (offset.y - this.y).pow(2)

/**
 * line formula: y = mx + b where m is the slope and b is the yIntercept
 * => m = y/x = (y1-y0)/(x1-x0)
 */
fun Offset.getSlopeTo(offset: Offset): Float {
    return (offset.y - this.y).div(offset.x - this.x)
}
