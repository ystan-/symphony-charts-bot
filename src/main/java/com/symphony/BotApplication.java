package com.symphony;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.service.message.model.Message;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;
import static com.symphony.bdk.core.activity.command.SlashCommand.slash;
import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromClasspath;

public class BotApplication {
    public static void main(String[] args) throws Exception {
        final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));

        bdk.activities().register(slash("/chart", false, context -> {
            byte[] chartData = getChart(getData());
            if (chartData == null) {
                return;
            }

            // Inline chart
            String base64Chart = Base64.getEncoder().encodeToString(chartData);
            bdk.messages().send(context.getStreamId(), "Inline Version:<br/><img src=\"data:image/png;base64, " + base64Chart + "\" />");

            // Attached chart
            bdk.messages().send(context.getStreamId(), Message.builder().content("Attachment Version:")
                .addAttachment(new ByteArrayInputStream(chartData), "chart.png").build());
        }));

        bdk.activities().register(slash("/chart2", false, context -> {
            Map<String, Integer> values = Map.of(
                "greenValue", 87,
                "yellowValue", 63,
                "redValue", 27,
                "greenPieValue", 17,
                "yellowPieValue", 63
            );
            String chart = bdk.messages().templates()
                .newTemplateFromClasspath("/charts.ftl")
                .process(values);
            bdk.messages().send(context.getStreamId(), chart);
        }));

        bdk.datafeed().start();
    }

    public static byte[] getChart(double[] data) {
        var dataset = new HistogramDataset();
        dataset.addSeries("Users", data, 50);

        JFreeChart histogram = ChartFactory.createHistogram(
            "Distribution of Usage", "Frequency", "Usage", dataset);

        try {
            return ChartUtils.encodeAsPNG(histogram.createBufferedImage(1000, 1000));
        } catch (IOException e) {
            return null;
        }
    }

    // Get from BI API
    public static double[] getData() {
      return new double[]{
          0.71477137, 0.55749811, 0.50809619, 0.47027228, 0.25281568, 0.66633175, 0.50676332, 0.6007552, 0.56892904, 0.49553407,
          0.61093935, 0.65057417, 0.40095626, 0.45969447, 0.51087888, 0.52894806, 0.49397198, 0.4267163, 0.54091298, 0.34545257,
          0.58548892, 0.3137885, 0.63521146, 0.57541744, 0.59862265, 0.66261386, 0.56744017, 0.42548488, 0.40841345, 0.47393027,
          0.60882106, 0.45961208, 0.43371424, 0.40876484, 0.64367337, 0.54092033, 0.34240811, 0.44048106, 0.48874236, 0.68300902,
          0.33563968, 0.58328107, 0.58054283, 0.64710522, 0.37801285, 0.36748982, 0.44386445, 0.47245989, 0.297599, 0.50295541,
          0.39785732, 0.51370486, 0.46650358, 0.5623638, 0.4446957, 0.52949791, 0.54611411, 0.41020067, 0.61644868, 0.47493691,
          0.50611458, 0.42518211, 0.45467712, 0.52438467, 0.724529, 0.59749142, 0.45940223, 0.53099928, 0.65159718, 0.38038268,
          0.51639554, 0.41847437, 0.46022878, 0.57326103, 0.44913632, 0.61043611, 0.42694949, 0.43997814, 0.58787928, 0.36252603,
          0.50937634, 0.47444256, 0.57992527, 0.29381335, 0.50357977, 0.42469464, 0.53049697, 0.7163579, 0.39741694, 0.41980533,
          0.68091159, 0.69330702, 0.50518926, 0.55884098, 0.48618324, 0.48469854, 0.55342267, 0.67159111, 0.62352006, 0.34773486
      };
    }
}
