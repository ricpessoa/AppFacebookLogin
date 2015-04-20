package login.facebook.rpessoa.appfacebooklogin;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookGraphResponseException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {
    private static String TAG = MainFragment.class.getCanonicalName();
    private TextView mTextDetails;
    private ProfilePictureView profilePictureView;
    private ShareButton shareButton;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;
    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.d(TAG, "onSuccess");
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            constructProfile(profile);

        }


        @Override
        public void onCancel() {
            Log.d(TAG, "onCancel");
        }

        @Override
        public void onError(FacebookException e) {
            Log.d(TAG, "onError " + e);
        }
    };
    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            //processDialogResults(null, true);
            Log.d(TAG, "onCancel");

        }

        @Override
        public void onError(FacebookException error) {
            if (error instanceof FacebookGraphResponseException) {
                FacebookGraphResponseException graphError =
                        (FacebookGraphResponseException) error;
                if (graphError.getGraphResponse() != null) {
                    //handleError(graphError.getGraphResponse());
                    Log.d(TAG, "onError " + error);
                    return;
                }
            }
            //processDialogError(error);
            Log.d(TAG, "onError " + error);

        }

        @Override
        public void onSuccess(Sharer.Result result) {
            //processDialogResults(result.getPostId(), false);
            Log.d(TAG, "onSuccess");

        }
    };
    private boolean canPresentShareDialogWithPhotos;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCallbackManager = CallbackManager.Factory.create();
        setupTokenTracker();
        setupProfileTracker();


        mTokenTracker.startTracking();
        mProfileTracker.startTracking();

        // Can we present the share dialog for photos?
        canPresentShareDialogWithPhotos = ShareDialog.canShow(SharePhotoContent.class);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupTextDetails(view);
        setupLoginButton(view);
        setupShareButton(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        constructProfile(profile);
    }

    @Override
    public void onStop() {
        super.onStop();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setupTextDetails(View view) {
        mTextDetails = (TextView) view.findViewById(R.id.profilename);
        profilePictureView = (ProfilePictureView) view.findViewById(R.id.profilePicture);
    }

    private void setupTokenTracker() {
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d(TAG, "" + currentAccessToken);
            }
        };
    }

    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.d(TAG, "" + currentProfile);
                constructProfile(currentProfile);
            }
        };
    }

    private void setupLoginButton(View view) {
        LoginButton mButtonLogin = (LoginButton) view.findViewById(R.id.login_button);
        mButtonLogin.setFragment(this);
        mButtonLogin.setReadPermissions("user_friends");
        mButtonLogin.registerCallback(mCallbackManager, mFacebookCallback);
    }

    private void setupShareButton(View view) {
        shareButton = (ShareButton) view.findViewById(R.id.share_button);
    }

    private void constructProfile(Profile profile) {
        if (profile != null) {
            mTextDetails.setText(profile.getName());
            profilePictureView.setProfileId(profile.getId());
            shareButton.setVisibility(View.VISIBLE);
        }else {
            profilePictureView.setProfileId(null);
            mTextDetails.setText(null);
            shareButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Share link
     */
//    private void setupShareButton(View view) {
//        ShareLinkContent content = new ShareLinkContent.Builder()
//                .setContentTitle("Hello Facebook")
//                .setContentUrl(Uri.parse("https://developers.facebook.com"))
//                .build();
//
//        ShareButton shareButton = (ShareButton)view.findViewById(R.id.share_button);
//        shareButton.setShareContent(content);
//    }

//
//    private ShareLinkContent getLinkContent() {
//        return new ShareLinkContent.Builder()
//                .setContentTitle("Aplication Sample")
//                        //.setContentUrl(Uri.parse(""))
//                .build();
//    }
//
//    private ShareOpenGraphContent getThrowActionContent() {
//        return new ShareOpenGraphContent.Builder()
//                .setAction(getThrowAction())
//                .setPreviewPropertyName(OpenGraphConsts.THROW_ACTION_PREVIEW_PROPERTY_NAME)
//                .build();
//    }
//
//    public void shareUsingNativeDialog() {
////        if (playerChoice == INVALID_CHOICE || computerChoice == INVALID_CHOICE) {
////            ShareContent content = getLinkContent();
////
////            // share the app
////            if (shareDialog.canShow(content, ShareDialog.Mode.NATIVE)) {
////                shareDialog.show(content, ShareDialog.Mode.NATIVE);
////            } else {
////                showError(R.string.native_share_error);
////            }
////        } else {
//        ShareContent content = getThrowActionContent();
//
//        if (shareDialog.canShow(content, ShareDialog.Mode.NATIVE)) {
//            shareDialog.show(content, ShareDialog.Mode.NATIVE);
//        } else {
//            showError(R.string.native_share_error);
//        }
////        }
//    }
//
//    private ShareOpenGraphAction getThrowAction() {
//        // The OG objects have their own bitmaps we could rely on, but in order to demonstrate
//        // attaching an in-memory bitmap (e.g., a game screencap) we'll send the bitmap explicitly
//        // ourselves.
//        //ImageButton view = gestureImages[playerChoice];
//        //BitmapDrawable drawable = (BitmapDrawable) view.getBackground();
//        //final Bitmap bitmap = drawable.getBitmap();
//        final Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.add_food);
//        return new ShareOpenGraphAction.Builder()
//                .setActionType(OpenGraphConsts.THROW_ACTION_TYPE)
//                .putString("fb_sample_rps:gesture", "12")
//                .putString("fb_sample_rps:opposing_gesture", "13")
//                .putPhotoArrayList("og:image", new ArrayList<SharePhoto>() {{
//                    add(new SharePhoto.Builder().setBitmap(bitmap).build());
//                }})
//                .build();
//    }
//
//    private void showError(int messageId) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(R.string.error_dialog_title).
//                setMessage(messageId).
//                setPositiveButton(R.string.error_ok_button, null);
//        builder.show();
//    }


}