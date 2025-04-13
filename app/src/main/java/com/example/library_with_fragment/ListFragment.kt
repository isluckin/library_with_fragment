//package com.example.library_with_fragment
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.library_with_fragment.databinding.FragmentListBinding
//
//class ListFragment : Fragment() {
//
//    private var _binding: FragmentListBinding? = null
//    private val binding get() = _binding!!
//    private val viewModel: ItemViewModel by activityViewModels()
//    private lateinit var adapter: ItemAdapter
//    private lateinit var layoutManager: LinearLayoutManager
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentListBinding.inflate(inflater, container, false)
//        layoutManager = LinearLayoutManager(requireContext())
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        adapter = ItemAdapter {
//            viewModel.selectItem(it)
//        }
//
//        binding.recyclerView.adapter = adapter
//        binding.recyclerView.layoutManager = layoutManager
//
//        viewModel.items.observe(viewLifecycleOwner) { items ->
//            adapter.submitList(items)
//        }
//
//        viewModel.scrollToLast.observe(viewLifecycleOwner) { scroll ->
//            if (scroll) {
//                val position = viewModel.scrollPosition.value ?: 0
//                binding.recyclerView.post {
//                    layoutManager.scrollToPosition(position)
//                    viewModel.resetScrollFlag()
//                }
//            }
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        val position = layoutManager.findFirstVisibleItemPosition()
//        viewModel.setScrollPos(position)
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
