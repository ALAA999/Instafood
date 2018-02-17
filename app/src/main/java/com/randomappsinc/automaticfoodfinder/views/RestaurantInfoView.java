package com.randomappsinc.automaticfoodfinder.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.randomappsinc.automaticfoodfinder.R;
import com.randomappsinc.automaticfoodfinder.models.Restaurant;
import com.squareup.picasso.Picasso;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantInfoView {

    @BindView(R.id.restaurant_thumbnail) ImageView thumbnail;
    @BindView(R.id.restaurant_name) TextView name;
    @BindView(R.id.rating) ImageView rating;
    @BindView(R.id.num_reviews) TextView numReviews;
    @BindView(R.id.restaurant_address) TextView address;
    @BindView(R.id.categories) TextView categories;
    @BindView(R.id.distance) TextView distance;
    @BindView(R.id.price) TextView price;

    @BindView(R.id.distance_container) View distanceContainer;
    @BindView(R.id.restaurant_info) View restaurantInfo;

    @BindView(R.id.thumbnail_stub) View thumbnailStub;
    @BindView(R.id.info_stub) View infoStub;
    @BindView(R.id.distance_stub) View distanceStub;

    @BindDrawable(R.drawable.gray_border) Drawable grayBorder;

    private Context context;
    private Drawable defaultThumbnail;

    public RestaurantInfoView(Context context, View view, Drawable defaultThumbnail) {
        this.context = context;
        this.defaultThumbnail = defaultThumbnail;
        ButterKnife.bind(this, view);
    }

    public void setSkeletonVisibility(boolean skeletonVisible) {
        int newSkeletonVisibility = skeletonVisible ? View.VISIBLE : View.GONE;
        thumbnailStub.setVisibility(newSkeletonVisibility);
        infoStub.setVisibility(newSkeletonVisibility);
        distanceStub.setVisibility(newSkeletonVisibility);

        int newRestaurantInfoVisibility = skeletonVisible ? View.GONE : View.VISIBLE;
        thumbnail.setVisibility(newRestaurantInfoVisibility);
        restaurantInfo.setVisibility(newRestaurantInfoVisibility);
        distanceContainer.setVisibility(newRestaurantInfoVisibility);
    }

    public void loadRestaurant(Restaurant restaurant) {
        setSkeletonVisibility(false);

        String restaurantImageUrl = restaurant.getImageUrl();
        if (restaurantImageUrl != null && !restaurantImageUrl.isEmpty()) {
            thumbnail.setBackground(null);
            Picasso.with(context)
                    .load(restaurantImageUrl)
                    .error(defaultThumbnail)
                    .fit().centerCrop()
                    .into(thumbnail);
        } else {
            thumbnail.setBackground(grayBorder);
            thumbnail.setImageDrawable(defaultThumbnail);
        }
        name.setText(restaurant.getName());
        Picasso.with(context)
                .load(UIUtils.getRatingDrawableId(restaurant.getRating()))
                .into(rating);

        String numReviewsText = restaurant.getReviewCount() == 1
                ? context.getString(R.string.one_review)
                : String.format(context.getString(R.string.num_reviews), restaurant.getReviewCount());
        numReviews.setText(numReviewsText);

        address.setText(restaurant.getAddress());

        String categoriesText = restaurant.getCategoriesListText();
        if (categoriesText.isEmpty()) {
            categories.setVisibility(View.GONE);
        } else {
            categories.setText(categoriesText);
            categories.setVisibility(View.VISIBLE);
        }

        String distanceTemplate = context.getString(R.string.miles_away);
        String distanceText = String.format(distanceTemplate, restaurant.getDistance());
        distance.setText(distanceText);

        String restaurantPrice = restaurant.getPrice();
        if (restaurantPrice == null || restaurantPrice.isEmpty()) {
            price.setVisibility(View.GONE);
        } else {
            price.setText(restaurant.getPrice());
            price.setVisibility(View.VISIBLE);
        }
    }
}
