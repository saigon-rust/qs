using CommunityToolkit.Mvvm.Input;
using ThaoNguyen.Models;

namespace ThaoNguyen.PageModels
{
    public interface IProjectTaskPageModel
    {
        IAsyncRelayCommand<ProjectTask> NavigateToTaskCommand { get; }
        bool IsBusy { get; }
    }
}