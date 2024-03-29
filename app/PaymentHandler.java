import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import spark.Request;
import spark.Response;
import static spark.Spark.post;

public class PaymentHandler {
    public static void main(String[] args) {
        Stripe.apiKey = "sk_test_your_secret_key";

        post("/create-payment-intent", (req, res) -> {
            PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                    .setCurrency("cad")
                    .setAmount(1000L) // Amount in cents
                    .build();
            PaymentIntent intent = PaymentIntent.create(createParams);
            return intent.toJson();
        });
    }
}
