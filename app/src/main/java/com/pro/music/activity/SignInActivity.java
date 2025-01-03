package com.pro.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.pro.music.MyApplication;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.music.MyApplication;
import com.pro.music.R;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.ActivitySignInBinding;
import com.pro.music.databinding.FragmentAdminAccountBinding;
import com.pro.music.fragment.admin.AdminAccountFragment;
import com.pro.music.model.User;
import com.pro.music.prefs.DataStoreManager;
import com.pro.music.utils.StringUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends BaseActivity {

    private ActivitySignInBinding mActivitySignInBinding;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySignInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(mActivitySignInBinding.getRoot());

        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Thiết lập sự kiện cho nút đăng nhập Google
        mActivitySignInBinding.btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());

        initListener();
    }

    private static final int RC_SIGN_IN = 9001;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Kết quả từ Google Sign-In
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Đăng nhập thành công với Google
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Xử lý lỗi
                Toast.makeText(SignInActivity.this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        DatabaseReference premiumRef = FirebaseDatabase.getInstance().getReference("premium");

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            // Lưu thông tin người dùng
                            User userObject = new User(user.getEmail(), "");
                            premiumRef.orderByChild("email").equalTo(user.getEmail())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                // Lặp qua để tìm email (trường hợp có nhiều kết quả)
                                                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                                    Boolean isPremium = childSnapshot.child("premium").getValue(Boolean.class);
                                                    userObject.setPremium(isPremium != null && isPremium);
                                                    break;  // Chỉ cần lấy 1 giá trị
                                                }
                                            } else {
                                                userObject.setPremium(false);
                                            }

                                            // Lưu vào SharedPreferences
                                            DataStoreManager.setUser(userObject);
                                            goToMainActivity();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(SignInActivity.this, "Failed to fetch premium status", Toast.LENGTH_SHORT).show();
                                            goToMainActivity();  // Vẫn cho vào nhưng với isPremium = false
                                        }
                                    });
                        }
                    } else {
                        // Xử lý lỗi chi tiết
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Authentication failed";
                        Toast.makeText(SignInActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initListener() {
//        mActivitySignInBinding.rdbUser.setChecked(true);
        mActivitySignInBinding.layoutSignUp.setOnClickListener(v ->
                GlobalFunction.startActivity(SignInActivity.this, SignUpActivity.class));

        // Sự kiện khi người dùng nhấn nút đăng nhập thông qua email/mật khẩu
        mActivitySignInBinding.btnSignIn.setOnClickListener(v -> onClickValidateSignIn());
        mActivitySignInBinding.tvForgotPassword.setOnClickListener(v -> onClickForgotPassword());
    }

    //run and get the data from the activity
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void onClickForgotPassword() {
        GlobalFunction.startActivity(this, ForgotPasswordActivity.class);
    }

    private void onClickValidateSignIn() {
        String strEmail = mActivitySignInBinding.edtEmail.getText().toString().trim();
        String strPassword = mActivitySignInBinding.edtPassword.getText().toString().trim();

        if (StringUtil.isEmpty(strEmail)) {
            Toast.makeText(SignInActivity.this, getString(R.string.msg_email_require), Toast.LENGTH_SHORT).show();
        } else if (StringUtil.isEmpty(strPassword)) {
            Toast.makeText(SignInActivity.this, getString(R.string.msg_password_require), Toast.LENGTH_SHORT).show();
        } else if (!StringUtil.isValidEmail(strEmail)) {
            Toast.makeText(SignInActivity.this, getString(R.string.msg_email_invalid), Toast.LENGTH_SHORT).show();
        } else {
            if (strEmail.contains(Constant.ADMIN_EMAIL_FORMAT) || strEmail.contains(Constant.STAFF_EMAIL_FORMAT)) {
                if (!strEmail.contains(Constant.ADMIN_EMAIL_FORMAT) && !strEmail.contains(Constant.STAFF_EMAIL_FORMAT)) {
                    Toast.makeText(SignInActivity.this, getString(R.string.msg_email_invalid_admin), Toast.LENGTH_SHORT).show();
                } else {
                    signInUser(strEmail, strPassword);
                }
            }
            else{
                signInUser(strEmail, strPassword);
            }
        }
    }

    private void signInUser(String email, String password) {
        showProgressDialog(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference premiumRef = FirebaseDatabase.getInstance().getReference("premium");

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showProgressDialog(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            User userObject = new User(user.getEmail(), password);
                            if (user.getEmail() != null && user.getEmail().contains(Constant.ADMIN_EMAIL_FORMAT)) {
                                userObject.setAdmin(true);
                            }
                            if (user.getEmail() != null && user.getEmail().contains(Constant.STAFF_EMAIL_FORMAT)) {
                                userObject.setAdmin(true);
                            }

                            premiumRef.orderByChild("email").equalTo(user.getEmail())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                // Lặp qua để tìm email (trường hợp có nhiều kết quả)
                                                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                                    Boolean isPremium = childSnapshot.child("premium").getValue(Boolean.class);
                                                    userObject.setPremium(isPremium != null && isPremium);
                                                    break;  // Chỉ cần lấy 1 giá trị
                                                }
                                            } else {
                                                userObject.setPremium(false);
                                            }

                                            // Lưu vào SharedPreferences
                                            DataStoreManager.setUser(userObject);
                                            goToMainActivity();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(SignInActivity.this, "Failed to fetch premium status", Toast.LENGTH_SHORT).show();
                                            goToMainActivity();  // Vẫn cho vào nhưng với isPremium = false
                                }
                            });
                        }
                    } else {
                        Toast.makeText(SignInActivity.this, getString(R.string.msg_sign_in_error),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToMainActivity() {
        if (DataStoreManager.getUser().isAdmin()) {
            GlobalFunction.startActivity(SignInActivity.this, AdminMainActivity.class);
        } else {
            GlobalFunction.startActivity(SignInActivity.this, MainActivity.class);
        }
        finishAffinity(); // Close all activities in the current task stack -> immediately close the app
    }
}