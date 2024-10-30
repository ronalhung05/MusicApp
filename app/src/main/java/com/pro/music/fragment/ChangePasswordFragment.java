package com.pro.music.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pro.music.R;
import com.pro.music.activity.MainActivity;
import com.pro.music.databinding.FragmentChangePasswordBinding;
import com.pro.music.model.User;
import com.pro.music.prefs.DataStoreManager;
import com.pro.music.utils.StringUtil;

public class ChangePasswordFragment extends Fragment {

    private FragmentChangePasswordBinding mFragmentChangePasswordBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mFragmentChangePasswordBinding = FragmentChangePasswordBinding.inflate(inflater,
                container, false);

        initListener();

        return mFragmentChangePasswordBinding.getRoot();
    }

    private void initListener() {
        mFragmentChangePasswordBinding.btnChangePassword
                .setOnClickListener(v -> onClickValidateChangePassword());
    }

    private void onClickValidateChangePassword() {
        if (getActivity() == null) return;
        String strOldPassword = mFragmentChangePasswordBinding.edtOldPassword.getText().toString().trim();
        String strNewPassword = mFragmentChangePasswordBinding.edtNewPassword.getText().toString().trim();
        String strConfirmPassword = mFragmentChangePasswordBinding.edtConfirmPassword.getText().toString().trim();
        if (StringUtil.isEmpty(strOldPassword)) {
            Toast.makeText(getActivity(),
                    getString(R.string.msg_old_password_require), Toast.LENGTH_SHORT).show();
        } else if (StringUtil.isEmpty(strNewPassword)) {
            Toast.makeText(getActivity(),
                    getString(R.string.msg_new_password_require), Toast.LENGTH_SHORT).show();
        } else if (StringUtil.isEmpty(strConfirmPassword)) {
            Toast.makeText(getActivity(),
                    getString(R.string.msg_confirm_password_require), Toast.LENGTH_SHORT).show();
        } else if (!DataStoreManager.getUser().getPassword().equals(strOldPassword)) {
            Toast.makeText(getActivity(),
                    getString(R.string.msg_old_password_invalid), Toast.LENGTH_SHORT).show();
        } else if (!strNewPassword.equals(strConfirmPassword)) {
            Toast.makeText(getActivity(),
                    getString(R.string.msg_confirm_password_invalid), Toast.LENGTH_SHORT).show();
        } else if (strOldPassword.equals(strNewPassword)) {
            Toast.makeText(getActivity(),
                    getString(R.string.msg_new_password_invalid), Toast.LENGTH_SHORT).show();
        } else {
            changePassword(strNewPassword);
        }
    }

    private void changePassword(String newPassword) {
        if (getActivity() == null) return;
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showProgressDialog(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        user.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    mainActivity.showProgressDialog(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(mainActivity,
                                getString(R.string.msg_change_password_successfully),
                                Toast.LENGTH_SHORT).show();
                        User userLogin = DataStoreManager.getUser();
                        userLogin.setPassword(newPassword);
                        DataStoreManager.setUser(userLogin);
                        mFragmentChangePasswordBinding.edtOldPassword.setText("");
                        mFragmentChangePasswordBinding.edtNewPassword.setText("");
                        mFragmentChangePasswordBinding.edtConfirmPassword.setText("");
                    }
                });
    }
}
