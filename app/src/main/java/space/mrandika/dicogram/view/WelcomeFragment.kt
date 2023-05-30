package space.mrandika.dicogram.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import space.mrandika.dicogram.R
import space.mrandika.dicogram.databinding.FragmentWelcomeBinding
import space.mrandika.dicogram.view.auth.LoginFragment
import space.mrandika.dicogram.view.auth.RegisterFragment

@AndroidEntryPoint
class WelcomeFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            btnNavLogin.setOnClickListener(this@WelcomeFragment)
            btnNavRegister.setOnClickListener(this@WelcomeFragment)

            btnGuest.setOnClickListener {
                redirectToHome()
            }
        }

        playAnimation()
    }

    private fun playAnimation() {
        binding?.apply {
            val image = ObjectAnimator.ofFloat(imageDicogram, View.ALPHA, 1f).setDuration(500)
            val login = ObjectAnimator.ofFloat(btnNavLogin, View.ALPHA, 1f).setDuration(500)
            val signup = ObjectAnimator.ofFloat(btnNavRegister, View.ALPHA, 1f).setDuration(500)
            val guest = ObjectAnimator.ofFloat(btnGuest, View.ALPHA, 1f).setDuration(500)

            val together = AnimatorSet().apply {
                playTogether(login, signup)
            }

            AnimatorSet().apply {
                playSequentially(image, together, guest)
                start()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun redirectToHome() {
        val intent = Intent(activity, RootActivity::class.java)
        startActivity(intent)
    }

    override fun onClick(view: View) {
        val destinationFragment: Fragment = when (view.id) {
            R.id.btn_nav_login -> LoginFragment()
            R.id.btn_nav_register -> RegisterFragment()
            else -> WelcomeFragment()
        }

        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        )

        fragmentTransaction.apply {
            replace(
                R.id.frame_container,
                destinationFragment,
                destinationFragment::class.java.simpleName
            )

            addToBackStack(null)
            commit()
        }
    }
}