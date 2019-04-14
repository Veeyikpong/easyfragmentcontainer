package com.veeyikpong.easyfragmentcontainer

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentContainer @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    interface FragmentListener {
        fun onFragmentShow(f: Fragment)
        fun onFragmentDestroyed(f: Fragment)
    }

    private var mListener: FragmentListener? = null
    private var mFragmentManager: FragmentManager = ((context) as AppCompatActivity).supportFragmentManager
    lateinit var currentFragment: Fragment

    //Animations
    private var mEnterAnimation: Int = 0
    private var mExitAnimation: Int = 0

    init {
        val attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.FragmentContainer
        )

        mEnterAnimation = attributeArray.getResourceId(R.styleable.FragmentContainer_enterAnimation, 0)
        mExitAnimation = attributeArray.getResourceId(R.styleable.FragmentContainer_exitAnimation, 0)

        attributeArray.recycle()

        mFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentActivityCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                super.onFragmentActivityCreated(fm, f, savedInstanceState)
                super.onFragmentResumed(fm, f)
                if (mListener != null) {
                    mListener!!.onFragmentShow(f)
                }
                currentFragment = f
            }

            override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
                super.onFragmentViewDestroyed(fm, f)
                if (mListener != null) {
                    mListener!!.onFragmentDestroyed(f)
                }
            }
        }, true)
    }

    /**
     * Add custom fragment listener.
     * Please be noted that you need to maintain the currentFragment instance by yourself
     */
    @NonNull
    fun addFragmentListener(listener: FragmentListener) {
        this.mListener = listener
    }

    /**
     * @param animation Set the enter animation for this fragment container
     */
    @NonNull
    fun setEnterAnimation(animation: Int) {
        this.mEnterAnimation = animation
    }

    /**
     * @param animation Set the exit animation for this fragment container
     */
    @NonNull
    fun setExitAnimation(animation: Int) {
        this.mExitAnimation = animation
    }

    /**
     * @return Current fragment manager
     */
    fun getFragmentManager(): FragmentManager {
        return mFragmentManager
    }

    /**
     * @param fragment      The fragment to be replaced
     */
    @NonNull
    fun replaceFragment(fragment: Fragment) {

        val transaction = mFragmentManager
                .beginTransaction()

        transaction.setCustomAnimations(mEnterAnimation, mExitAnimation)

        transaction
                .replace(this.id, fragment)
                .disallowAddToBackStack()
                .commit()
    }

    /**
     * @param fragment      The fragment to be replaced
     */
    @NonNull
    fun replaceFragment(fragment: Fragment, enterAnimation: Int = 0, exitAnimation: Int = 0) {

        val transaction = mFragmentManager
                .beginTransaction()

        if (enterAnimation != 0 && exitAnimation != 0) {
            transaction.setCustomAnimations(enterAnimation, exitAnimation)
        } else if (enterAnimation != 0 && exitAnimation == 0) {
            transaction.setCustomAnimations(enterAnimation, mExitAnimation)
        } else if (exitAnimation != 0 && enterAnimation == 0) {
            transaction.setCustomAnimations(mEnterAnimation, exitAnimation)
        } else {
            transaction.setCustomAnimations(mEnterAnimation, mExitAnimation)
        }

        transaction
                .replace(this.id, fragment)
                .disallowAddToBackStack()
                .commit()
    }

    /**
     * This function adds a fragment with optional bundle arguments, enter animation and exit animation
     * @param fragment          The fragment to be added
     * @param bundle            OPTIONAL bundle arguments
     * @param enterAnimation    OPTIONAL enter animation. If not passing in, the animation from XML will be used
     * @param exitAnimation     OPTIONAL exit animation. if not passed in, the animation from XML will be used
     */
    @NonNull
    fun addFragment(fragment: Fragment, bundle: Bundle = Bundle(), enterAnimation: Int = 0, exitAnimation: Int = 0) {
        fragment.arguments = bundle

        val transaction = mFragmentManager
                .beginTransaction()

        if (enterAnimation != 0 && exitAnimation != 0) {
            transaction.setCustomAnimations(enterAnimation, exitAnimation)
        } else if (enterAnimation != 0 && exitAnimation == 0) {
            transaction.setCustomAnimations(enterAnimation, mExitAnimation)
        } else if (exitAnimation != 0 && enterAnimation == 0) {
            transaction.setCustomAnimations(mEnterAnimation, exitAnimation)
        } else {
            transaction.setCustomAnimations(mEnterAnimation, mExitAnimation)
        }

        transaction
                .replace(this.id, fragment, fragment.javaClass.simpleName)
                .addToBackStack(fragment.javaClass.simpleName)

        transaction.commit()
    }

    /**
     * This function adds a fragment with desired tag, optional bundle arguments, enter animation and exit animation
     * @param fragment          The fragment to be added
     * @param bundle            OPTIONAL bundle arguments
     * @param fragmentTag       Tag for the fragment to be added
     * @param enterAnimation    OPTIONAL enter animation. If not passing in, the animation from XML will be used
     * @param exitAnimation     OPTIONAL exit animation. if not passed in, the animation from XML will be used
     */
    @NonNull
    fun addFragment(
            fragment: Fragment,
            bundle: Bundle = Bundle(),
            fragmentTag: String = "",
            enterAnimation: Int = 0,
            exitAnimation: Int = 0
    ) {
        fragment.arguments = bundle

        var tag = fragment.javaClass.simpleName
        if (!fragmentTag.isBlank()) {
            tag = fragmentTag
        }

        val transaction = mFragmentManager
                .beginTransaction()

        if (enterAnimation != 0 && exitAnimation != 0) {
            transaction.setCustomAnimations(enterAnimation, exitAnimation)
        } else if (enterAnimation != 0 && exitAnimation == 0) {
            transaction.setCustomAnimations(enterAnimation, mExitAnimation)
        } else if (exitAnimation != 0 && enterAnimation == 0) {
            transaction.setCustomAnimations(mEnterAnimation, exitAnimation)
        } else {
            transaction.setCustomAnimations(mEnterAnimation, mExitAnimation)
        }

        transaction
                .replace(this.id, fragment, tag)
                .addToBackStack(tag)

        transaction.commit()
    }

    /**
     * Return to previous fragment
     * @return  true if a fragment is popped, false if current fragment is the last fragment.
     */
    fun back(): Boolean {
        val backStackCount = mFragmentManager.backStackEntryCount
        if (backStackCount > 1) {
            mFragmentManager.popBackStackImmediate()
            return true
        }

        return false
    }
}