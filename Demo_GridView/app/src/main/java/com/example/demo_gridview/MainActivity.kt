package com.example.demo_gridview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    val productNames = arrayOf("Gậy bẻ lò xo", "Tai nghe", "Điện thoại", "Laptop")
    val productPrices = arrayOf("1.000.000 đ", "200.000 đ", "5.000.000 đ", "15.000.000 đ")
    val productImages = arrayOf(
        R.drawable.hinh1,
        R.drawable.hinh2,
        R.drawable.hinh3,
        R.drawable.hinh4
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gv = findViewById<GridView>(R.id.gv)
        val adapter = ProductAdapter()
        gv.adapter = adapter

        gv.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, ProductActivity::class.java)
            intent.putExtra("name", productNames[position])
            intent.putExtra("price", productPrices[position])
            intent.putExtra("image", productImages[position])
            startActivity(intent)
        }
    }

    inner class ProductAdapter : BaseAdapter() {
        override fun getCount(): Int = productNames.size
        override fun getItem(position: Int): Any = productNames[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = convertView ?: LayoutInflater.from(this@MainActivity)
                .inflate(R.layout.layout_item, parent, false)

            val img = view.findViewById<ImageView>(R.id.imageView2)
            val name = view.findViewById<TextView>(R.id.txtName)
            val price = view.findViewById<TextView>(R.id.txtPrice)

            img.setImageResource(productImages[position])
            name.text = productNames[position]
            price.text = productPrices[position]

            return view
        }
    }
}
