package com.randomappsinc.instafood.views;

import android.view.View;
import android.widget.CheckBox;

import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.constants.PlaceAttribute;
import com.randomappsinc.instafood.models.Filter;
import com.randomappsinc.instafood.utils.UIUtils;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AttributePickerView {

    @BindView(R.id.hot_new_checkbox) CheckBox hotNewCheckbox;
    @BindView(R.id.gender_neutral_checkbox) CheckBox genderNeutralCheckbox;

    public AttributePickerView(View rootView) {
        ButterKnife.bind(this, rootView);
    }

    public Set<String> getAttributes() {
        Set<String> attributes = new HashSet<>();
        if (hotNewCheckbox.isChecked()) {
            attributes.add(PlaceAttribute.HOT_AND_NEW);
        }
        if (genderNeutralCheckbox.isChecked()) {
            attributes.add(PlaceAttribute.GENDER_NEUTRAL_RESTROOMS);
        }
        return attributes;
    }

    public void loadFilter(Filter filter) {
        Set<String> attributes = filter.getAttributes();
        boolean hotNewSelected = attributes.contains(PlaceAttribute.HOT_AND_NEW);
        UIUtils.setCheckedImmediately(hotNewCheckbox, hotNewSelected);

        boolean genderNeutralSelected = attributes.contains(PlaceAttribute.GENDER_NEUTRAL_RESTROOMS);
        UIUtils.setCheckedImmediately(genderNeutralCheckbox, genderNeutralSelected);
    }

    @OnClick(R.id.hot_new_container)
    public void onHotNewClicked() {
        boolean selected = hotNewCheckbox.isChecked();
        hotNewCheckbox.setChecked(!selected);
    }

    @OnClick(R.id.gender_neutral_container)
    public void onGenderNeutralClicked() {
        boolean selected = genderNeutralCheckbox.isChecked();
        genderNeutralCheckbox.setChecked(!selected);
    }
}
