package android.ext.app;

import android.app.Activity;
import android.content.Context;
import android.ext.core.Objects;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

/**
 * @author Oleksii Kropachov (o.kropachov@shamanland.com)
 */
public class FragmentExt extends Fragment implements CustomServiceResolver {
    private Context mContext;
    private CustomServiceResolver mCustomServiceResolver;

    protected void setCustomServiceResolver(CustomServiceResolver customServiceResolver) {
        mCustomServiceResolver = customServiceResolver;
    }

    public Context getContext() {
        return Objects.notNull(mContext);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = new ContextFragmentWrapper(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    public Object resolveCustomService(String name) {
        Object result = null;

        CustomServiceResolver resolver = mCustomServiceResolver;
        if (resolver != null) {
            result = resolver.resolveCustomService(name);
        }

        if (result == null) {
            Activity activity = getActivity();
            if (activity instanceof CustomServiceResolver) {
                result = ((CustomServiceResolver) activity).resolveCustomService(name);
            }
        }

        return result;
    }

    @Override
    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
        return LayoutInflater.from(getContext());
    }
}