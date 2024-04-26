package dk.itu.moapd.copenhagenbuzz.fcag.dbOperations

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

open class SwipeToDeleteCallback : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {



    /**
     * This method is used to manage drag and drop functionality.
     * Since reordering items is not needed for this implementation, it always returns false.
     *
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached.
     * @param viewHolder The ViewHolder being dragged.
     * @param target The ViewHolder over which the currently active item is being dragged.
     * @return Boolean false as this implementation does not support moving items.
     */
    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }



    /**
     * Called when an item is swiped left or right. By default, it displays a Snackbar message indicating
     * successful deletion. Override this method in subclasses to handle item deletion or other actions upon swiping.
     *
     * @param viewHolder The ViewHolder of the item swiped.
     * @param direction The direction in which the item was swiped. This can be either ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT.
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        Snackbar.make(viewHolder.itemView, "Item deleted successfully", Snackbar.LENGTH_SHORT).show()
    }


}