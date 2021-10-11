package com.health.mental.mooditude.activity.ui.tracking

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.core.M3AssessmentManager
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.entity.M3Assessment
import com.health.mental.mooditude.databinding.FragmentTrackingMainBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.fragment.InformationDlgFragment
import com.health.mental.mooditude.utils.CalendarUtils
import com.health.mental.mooditude.utils.SlideAnimationUtil
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainFragment(private var mCurrentMode: Int) : BaseFragment(),
    OptionsBottomSheetFragment.ItemClickListener {

    companion object {
        fun newInstance(mode: Int) = MainFragment(mode)
    }

    private val binding get() = _binding!! as FragmentTrackingMainBinding
    private var mLatestAssessment: M3Assessment? = null

    private var mPrimaryColor: Int = 0
    private var mSecondaryColor: Int = 0
    private var mTertaryColor: Int = 0
    private var mRegularFont: Typeface? = null


    private var mCurrentTabPosition = -1 //Week

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mPrimaryColor = ContextCompat.getColor(requireContext(), R.color.primaryColor)
        mSecondaryColor = ContextCompat.getColor(requireContext(), R.color.secondaryColor)
        mTertaryColor = ContextCompat.getColor(requireContext(), R.color.tertaryColor)
        mRegularFont = ResourcesCompat.getFont(requireContext(), R.font.circularstd_book)

        _binding = FragmentTrackingMainBinding.inflate(inflater, container, false)
        val root: View = binding.root


        (requireActivity() as BaseActivity).checkForNewAssessment(binding.tabWeek.layoutAddAssessment.root)
        //select tabs
        initTabLayout()

        binding.tabWeek.assessmentTopbar.root.setOnClickListener {
            //(requireActivity() as BaseActivity).showAssessmentScoreDetailedPage()
            (parentFragment as TrackingFragment).onViewFullReportBtnClicked(mLatestAssessment!!)
        }

        binding.tabWeek.ivMore.setOnClickListener {
            //(requireActivity() as BaseActivity).showAssessmentScoreDetailedPage()
            showPopup(it, 0)
        }

        binding.tabMonth.ivMore.setOnClickListener {
            //(requireActivity() as BaseActivity).showAssessmentScoreDetailedPage()
            showPopup(it, 1)
        }

        binding.tabQuarter.ivMore.setOnClickListener {
            //(requireActivity() as BaseActivity).showAssessmentScoreDetailedPage()
            showPopup(it, 1)
        }

        binding.tabWeek.assessmentTopbar.tvScore.setOnClickListener {
            //(requireActivity() as BaseActivity).showAssessmentScoreDetailedPage()
            (parentFragment as TrackingFragment).onViewScoreBtnClicked(mLatestAssessment!!)
        }
        //Default tab is weeks
        binding.tvWeeks.callOnClick()

        //initUi()
        DBManager.instance.getLastestAssessment().observe(viewLifecycleOwner, Observer {
            mLatestAssessment = it
            if (mLatestAssessment != null) {

                binding.tabWeek.viewAssessment.visibility = View.VISIBLE
                (requireActivity() as BaseActivity).setupAssessmentTopbar(
                    mLatestAssessment!!,
                    binding.tabWeek.assessmentTopbar
                )

                (requireActivity() as BaseActivity).setupAssessmentTopbarForSharing(
                    mLatestAssessment!!,
                    binding.tabWeek.assessmentTopbarReplica
                )

                //now check for mode of page
                //check for mode and open page
                if (mCurrentMode == 1) {
                    showFullReport()
                }
                mCurrentMode = 0
            } else {
                binding.tabWeek.viewAssessment.visibility = View.GONE
            }
        })

        //initialize month tab
        setupMonthTab()
        setupQuarterTab()

        return root
    }

    private object mLeftAxisValueFormaater : IndexAxisValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString() + "%"
        }
    }

    private fun setupMonthTab() {

        DBManager.instance.getListForMonth().observe(viewLifecycleOwner, Observer {

            debugLog(TAG, "Assessment list size : " + it.size)
            val list = it

            if (list != null && list.size > 0) {
                binding.tabMonth.viewNoData.root.visibility = View.GONE
                binding.tabMonth.tvTitle.visibility = View.VISIBLE
                binding.tabMonth.ivMore.visibility = View.VISIBLE
                binding.tabMonth.chart1.visibility = View.VISIBLE
                val chart = binding.tabMonth.chart1
                setupAssessmentChart(chart, list)
            } else {
                binding.tabMonth.viewNoData.root.visibility = View.VISIBLE
                binding.tabMonth.tvTitle.visibility = View.GONE
                binding.tabMonth.ivMore.visibility = View.GONE
                binding.tabMonth.chart1.visibility = View.GONE
            }
        })
    }

    private fun setupQuarterTab() {

        DBManager.instance.getListForQuarter().observe(viewLifecycleOwner, Observer {

            debugLog(TAG, "Quarter Assessment list size : " + it.size)
            val list = it

            if (list != null && list.size > 0) {
                binding.tabQuarter.viewNoData.root.visibility = View.GONE
                binding.tabQuarter.tvTitle.visibility = View.VISIBLE
                binding.tabQuarter.ivMore.visibility = View.VISIBLE
                binding.tabQuarter.chart1.visibility = View.VISIBLE
                val chart = binding.tabQuarter.chart1
                setupAssessmentChart(chart, list)
            } else {
                binding.tabQuarter.viewNoData.root.visibility = View.VISIBLE
                binding.tabQuarter.tvTitle.visibility = View.GONE
                binding.tabQuarter.ivMore.visibility = View.GONE
                binding.tabQuarter.chart1.visibility = View.GONE
            }
        })
    }

    private fun setupAssessmentChart(chart: CombinedChart, list: List<M3Assessment>) {
        //Set chartview with default properties
        setupChartView(chart)

        //customize legend view
        setupChartLegend(chart)

        //Customize x-axis
        setupChartXAxis(chart)

        //Customize left-axis
        setupChartLeftAxis(chart)

        //Contains entry for each type
        val entriesD = ArrayList<BarEntry>()
        val entriesA = ArrayList<BarEntry>()
        val entriesP = ArrayList<BarEntry>()
        val entriesB = ArrayList<BarEntry>()
        val entryX = ArrayList<String>()

        val lineEntries = ArrayList<Entry>()
        var index = 0f

        var lastMonth = -1
        for (assessment in list) {

            debugLog(TAG, "Assessement score : " + assessment.allScore)

            //For all score
            lineEntries.add(
                Entry(
                    index,
                    M3AssessmentManager.getPercentageForAllScore(assessment.allScore)
                )
            )

            //Depression
            entriesD.add(
                BarEntry(
                    index,
                    floatArrayOf(
                        M3AssessmentManager.getPercentageForDepression(assessment.depressionScore)
                    )
                )
            )

            //Anxiety
            entriesA.add(
                BarEntry(
                    index,
                    floatArrayOf(
                        M3AssessmentManager.getPercentageForAnxiety(assessment.getAnxietyScore())

                    )
                )
            )

            //PTSD
            entriesP.add(
                BarEntry(
                    index,
                    floatArrayOf(
                        M3AssessmentManager.getPercentageForPTSD(assessment.ptsdScore)
                    )
                )
            )

            //Bipolar
            entriesB.add(
                BarEntry(
                    index,
                    floatArrayOf(
                        M3AssessmentManager.getPercentageForBipolar(assessment.bipolarScore)
                    )
                )
            )

            val date = assessment.createDate
            val month = CalendarUtils.getMonth(date)

            if (lastMonth == -1 || lastMonth == month) {
                lastMonth = month
                entryX.add(SimpleDateFormat("dd", Locale.US).format(assessment.createDate))
            } else {
                lastMonth = month
                entryX.add(SimpleDateFormat("MMM", Locale.US).format(assessment.createDate))
            }

            debugLog(TAG, "Added X Value : " + entryX.get(index.toInt()))
            index++
        }

        //set value formatter for x axis
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(entryX)
        chart.axisLeft.valueFormatter = mLeftAxisValueFormaater

        //line data
        val lineData = LineData()

        val lineSet = LineDataSet(lineEntries, getString(R.string.text_overall_score))
        lineSet.color = ContextCompat.getColor(requireContext(), R.color.overall_score)
        lineSet.lineWidth = 2.5f
        lineSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.overall_score))
        lineSet.circleRadius = 8f
        lineSet.fillColor = ContextCompat.getColor(requireContext(), R.color.overall_score)
        lineSet.mode = LineDataSet.Mode.LINEAR
        lineSet.form = Legend.LegendForm.SQUARE
        lineSet.setDrawValues(false)
        lineSet.axisDependency = YAxis.AxisDependency.LEFT
        lineData.addDataSet(lineSet)

        //Create data set for each entry
        val set1 = BarDataSet(entriesD, requireContext().getString(R.string.text_depression))
        set1.color = ContextCompat.getColor(requireContext(), R.color.risk_depression)
        val set2 = BarDataSet(entriesA, requireContext().getString(R.string.text_anxiety))
        set2.color = ContextCompat.getColor(requireContext(), R.color.risk_anxiety)
        val set3 = BarDataSet(entriesP, requireContext().getString(R.string.text_ptsd))
        set3.color = ContextCompat.getColor(requireContext(), R.color.risk_ptsd)
        val set4 = BarDataSet(entriesB, requireContext().getString(R.string.text_bipolar))
        set4.color = ContextCompat.getColor(requireContext(), R.color.risk_bipolar)

        set1.isHighlightEnabled = false
        set2.isHighlightEnabled = false
        set3.isHighlightEnabled = false
        set4.isHighlightEnabled = false

        set1.setDrawValues(false)
        set2.setDrawValues(false)
        set3.setDrawValues(false)
        set4.setDrawValues(false)

        //Create bardata
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set1)
        dataSets.add(set2)
        dataSets.add(set3)
        dataSets.add(set4)
        val barData = BarData(dataSets)

        val groupSpace = 0.2f
        val barSpace = 0f // x3 DataSet
        val barWidth = 0.2f // x3 DataSet
        // (0.2 + 0) * 3 + 0.4 = 1.00 -> interval per "group"
        // (barSpace + barWidth) * 2 + groupSpace = 1

        barData.barWidth = barWidth
        barData.groupBars(0f, groupSpace, barSpace);

        // so that the entire chart is shown when scrolled from right to left -1.1f
        var size = entryX.size.toFloat()
        if (size < 5) {
            size = 5f
        }
        chart.xAxis.axisMaximum = size

        val data1 = CombinedData()

        data1.setData(lineData)
        data1.setData(barData)
        data1.setDrawValues(false)
        // data.setValueFormatter(ChartValueFormatter())
        //data.setValueTextColor(Color.WHITE)
        //data1.barWidth = 0.5f

        chart.data = data1
        chart.invalidate()
    }

    private fun setupChartLeftAxis(chart: CombinedChart) {
        val tertaryColor = ContextCompat.getColor(requireContext(), R.color.tertaryColor)
        val leftAxis = chart.axisLeft
        //leftAxis.typeface = tfLight
        leftAxis.valueFormatter = LargeValueFormatter()
        leftAxis.setDrawGridLines(true)
        leftAxis.spaceTop = 35f
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)
        leftAxis.axisMaximum = 100f
        leftAxis.gridColor = tertaryColor

        leftAxis.textColor = mSecondaryColor
        leftAxis.typeface = mRegularFont
        leftAxis.textSize = requireContext().resources.getDimension(R.dimen._4ssp)
    }

    private fun setupChartXAxis(chart: CombinedChart) {
        val xAxis = chart.xAxis
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawGridLines(true)
        xAxis.gridColor = mTertaryColor
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis.textColor = mSecondaryColor
        xAxis.typeface = mRegularFont
        xAxis.textSize = requireContext().resources.getDimension(R.dimen._4ssp)
    }

    private fun setupChartLegend(chart: CombinedChart) {

        val l = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.form = Legend.LegendForm.CIRCLE
        l.textColor = mPrimaryColor
        l.typeface = mRegularFont
        l.textSize = requireContext().resources.getDimension(R.dimen._4ssp)
        l.isWordWrapEnabled = true
    }

    private fun setupChartView(chart: CombinedChart) {
        chart.extraBottomOffset = 30f
        chart.axisRight.isEnabled = false
        chart.getDescription().setEnabled(false);

        chart.isHighlightPerTapEnabled = false
        chart.isHighlightFullBarEnabled = false
        chart.isHighlightPerDragEnabled = false

        // scaling can now only be done on x- and y-axis separately
        chart.setScaleEnabled(false)
        chart.setPinchZoom(false);

        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
    }


    private fun initTabLayout() {
        binding.tvWeeks.setOnClickListener {

            if (mCurrentTabPosition == 0) {
                return@setOnClickListener
            }
            binding.tvWeeks.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primaryColor
                )
            )

            //month is selected
            if (mCurrentTabPosition == -1) {

                binding.seperatorWeek.visibility = View.VISIBLE
                binding.tabWeek.root.visibility = View.VISIBLE
                //SlideAnimationUtil.slideInFromLeft(requireContext(), binding.tabWeek.root)
            } else if (mCurrentTabPosition == 1) {
                SlideAnimationUtil.slideOutToRight(requireContext(), binding.tabMonth.root)


                binding.seperatorMonth.visibility = View.INVISIBLE
                binding.tabMonth.root.visibility = View.GONE

                binding.seperatorWeek.visibility = View.VISIBLE
                binding.tabWeek.root.visibility = View.VISIBLE
                SlideAnimationUtil.slideInFromLeft(requireContext(), binding.tabWeek.root)

                binding.tvMonth.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.tertaryColor
                    )
                )
            } else if (mCurrentTabPosition == 2) {
                SlideAnimationUtil.slideOutToRight(requireContext(), binding.tabQuarter.root)

                binding.seperatorQuarter.visibility = View.INVISIBLE
                binding.tabQuarter.root.visibility = View.GONE

                binding.seperatorWeek.visibility = View.VISIBLE
                binding.tabWeek.root.visibility = View.VISIBLE
                SlideAnimationUtil.slideInFromLeft(requireContext(), binding.tabWeek.root)

                binding.tvQuarter.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.tertaryColor
                    )
                )
            }

            mCurrentTabPosition = 0
        }

        binding.tvMonth.setOnClickListener {

            if (mCurrentTabPosition == 1) {
                return@setOnClickListener
            }
            binding.tvMonth.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primaryColor
                )
            )

            if (mCurrentTabPosition == -1) {

                binding.seperatorMonth.visibility = View.VISIBLE
                binding.tabMonth.root.visibility = View.VISIBLE
                //SlideAnimationUtil.slideInFromLeft(requireContext(), binding.tabMonth.root)
            }
            //month is selected
            else if (mCurrentTabPosition == 0) {
                SlideAnimationUtil.slideOutToLeft(requireContext(), binding.tabWeek.root)


                binding.seperatorWeek.visibility = View.INVISIBLE
                binding.tabWeek.root.visibility = View.GONE

                binding.seperatorMonth.visibility = View.VISIBLE
                binding.tabMonth.root.visibility = View.VISIBLE
                SlideAnimationUtil.slideInFromRight(requireContext(), binding.tabMonth.root)

                binding.tvWeeks.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.tertaryColor
                    )
                )
            } else if (mCurrentTabPosition == 2) {
                SlideAnimationUtil.slideOutToRight(requireContext(), binding.tabQuarter.root)

                binding.seperatorQuarter.visibility = View.INVISIBLE
                binding.tabQuarter.root.visibility = View.GONE

                binding.seperatorMonth.visibility = View.VISIBLE
                binding.tabMonth.root.visibility = View.VISIBLE
                SlideAnimationUtil.slideInFromLeft(requireContext(), binding.tabMonth.root)

                binding.tvQuarter.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.tertaryColor
                    )
                )
            }

            mCurrentTabPosition = 1
        }

        binding.tvQuarter.setOnClickListener {
            if (mCurrentTabPosition == 2) {
                return@setOnClickListener
            }
            binding.tvQuarter.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primaryColor
                )
            )

            //month is selected
            if (mCurrentTabPosition == -1) {
                binding.seperatorQuarter.visibility = View.VISIBLE
                binding.tabQuarter.root.visibility = View.VISIBLE
                // SlideAnimationUtil.slideInFromRight(requireContext(), binding.tabQuarter.root)

            } else if (mCurrentTabPosition == 0) {
                SlideAnimationUtil.slideOutToLeft(requireContext(), binding.tabWeek.root)


                binding.seperatorWeek.visibility = View.INVISIBLE
                binding.tabWeek.root.visibility = View.GONE

                binding.seperatorQuarter.visibility = View.VISIBLE
                binding.tabQuarter.root.visibility = View.VISIBLE
                SlideAnimationUtil.slideInFromRight(requireContext(), binding.tabMonth.root)

                binding.tvWeeks.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.tertaryColor
                    )
                )
            } else if (mCurrentTabPosition == 1) {
                SlideAnimationUtil.slideOutToLeft(requireContext(), binding.tabMonth.root)

                binding.seperatorMonth.visibility = View.INVISIBLE
                binding.tabMonth.root.visibility = View.GONE

                binding.seperatorQuarter.visibility = View.VISIBLE
                binding.tabQuarter.root.visibility = View.VISIBLE
                SlideAnimationUtil.slideInFromLeft(requireContext(), binding.tabQuarter.root)

                binding.tvMonth.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.tertaryColor
                    )
                )
            }

            mCurrentTabPosition = 2
        }
    }


    fun showPopup(v: View, mode: Int) {

        childFragmentManager.let {
            val bundle = Bundle()
            bundle.putInt("mode", mode)
            OptionsBottomSheetFragment.newInstance(bundle, this).apply {
                show(it, tag)
            }
        }
    }

    fun showFullReport() {
        if (mCurrentTabPosition == 0 && binding.tabWeek.assessmentTopbar.root.visibility == View.VISIBLE) {
            binding.tabWeek.assessmentTopbar.root.callOnClick()
        }
    }

    override fun onItemClick(itemId: Int) {
        when (itemId) {
            R.id.menu_add_new -> {
                (requireActivity() as BaseActivity).startM3Assessment(false)
            }
            R.id.menu_see_past_assessment -> {
                //show all
                (parentFragment as TrackingFragment).onShowListBtnClicked()
            }
            R.id.menu_full_report -> {
                //show all
                binding.tabWeek.assessmentTopbar.root.callOnClick()
            }
            /*R.id.menu_share -> {
                //show all
                if(mCurrentTabPosition == 0) {
                    UiUtils.shareAssessment(binding.tabWeek.assessmentTopbarReplica.root, requireActivity())
                }
                else if(mCurrentTabPosition == 1) {
                    UiUtils.shareAssessment(binding.tabMonth.chart1, requireActivity())
                }
            }*/
            R.id.menu_science -> {
                //Science behind the assessment
                requireActivity().supportFragmentManager.let {
                    InformationDlgFragment().apply {
                        show(it, tag)
                    }
                }
            }
        }
    }
}



