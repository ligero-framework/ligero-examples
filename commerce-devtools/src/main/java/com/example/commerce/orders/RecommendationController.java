package com.example.commerce.orders;

import com.ligero.Ligero;
import com.ligero.beans.stereotype.Controller;

@Controller
public class RecommendationController {

    private final RecommendationService recommendations;

    public RecommendationController(RecommendationService recommendations) {
        this.recommendations = recommendations;
    }

    public void register(Ligero app) {
        app.get("/api/customers/{id}/recommendations",
            ctx -> ctx.json(recommendations.forCustomer(ctx.pathParamAsLong("id"))));
    }
}
