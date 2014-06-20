package android.ext.eventbus;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.ext.core.ContextOwner;
import android.ext.core.Objects;
import android.ext.customservice.CustomService;
import android.ext.inflater.Inflatable;
import android.os.Bundle;
import android.util.AttributeSet;

import org.xmlpull.v1.XmlPullParser;

public class EventDelivery implements EventDispatcher, Inflatable {
    private final Context mContext;
    private final FragmentManager mManager;
    private EventDeliveryOptions mOptions;

    public EventDeliveryOptions getOptions() {
        return mOptions;
    }

    public void setOptions(EventDeliveryOptions options) {
        mOptions = options;
    }

    public EventDelivery(Context context, FragmentManager manager) {
        mContext = Objects.notNull(context);
        mManager = Objects.notNull(manager);
    }

    @Override
    public boolean onNewEvent(int eventId, Bundle event) {
        Fragment fragment = mManager.findFragmentByTag(mOptions.tag);
        if (fragment != null) {
            if (!fragment.isVisible()) {
                mOptions.performTransaction(mManager, fragment);
            }

            if (fragment instanceof ContextOwner) {
                EventDispatcher dispatcher = CustomService.get(((ContextOwner) fragment).getContext(), EventDispatcher.class);
                if (dispatcher != null) {
                    return dispatcher.onNewEvent(eventId, event);
                }
            }

            return false;
        } else {
            mOptions.performTransaction(mManager, Fragment.instantiate(mContext, mOptions.fragment, EventBus.prepare(eventId, event)));
            return true;
        }
    }

    @Override
    public void inflate(Context context, XmlPullParser parser, AttributeSet attrs) {
        setOptions(new EventDeliveryOptions(context, attrs));
    }
}