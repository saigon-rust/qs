using ThaoNguyen.Models;
using ThaoNguyen.PageModels;

namespace ThaoNguyen.Pages
{
    public partial class MainPage : ContentPage
    {
        public MainPage(MainPageModel model)
        {
            InitializeComponent();
            BindingContext = model;
        }
    }
}