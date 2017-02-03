import {EVENTS} from './../../constants';
import {dom} from './../../../../../utilities/datatables.utilities';

function TableController(DTOptionsBuilder,
                         DTColumnBuilder,
                         LinelistTableService,
                         $scope, $compile) {
  this.dtOptions = DTOptionsBuilder
    .fromFnPromise(function() {
      return LinelistTableService.getMetadata();
    })
    .withDOM(dom)
    .withScroller()
    .withOption('scrollX', true)
    .withOption('deferRender', true)
    .withOption('scrollY', '50vh')
    .withOption('scrollCollapse', true)
    .withColReorder()
    .withColReorderCallback(function() {
      $scope.$broadcast(EVENTS.TABLE.colReorder, {columns: this.fnOrder()});
    })
    .withOption('drawCallback', () => {
      // This adds the tools to handle meta data header hiding, template selection and saving.
      // Datatables will add this after the table is created to we need the $compile so that
      // angularjs can grab hold of it.
      const div = document.querySelector('.toolbar');
      // Make sure this only gets added once
      if (div.getElementsByTagName('metadata-component').length === 0) {
        div.innerHTML = `
  <metadata-component></metadata-component>`;
        $compile(div)($scope);
      }
    });

  const headers = LinelistTableService.getColumns();

  this.dtColumns = headers.map(header => {
    return DTColumnBuilder
      .newColumn(header)
      .withTitle(header)
      .renderWith(data => {
        // This is where any custom rendering logic should go.`
        // example formatting date columns.
        return data.value;
      });
  });

  this.toggleFieldVisibility = column => {
    if (column) {
      this.dtColumns[column.index].visible = column.selected;
    }
  };
}

TableController.$inject = [
  'DTOptionsBuilder',
  'DTColumnBuilder',
  'LinelistTableService',
  '$scope',
  '$compile'
];

export const TableComponent = {
  template: `
<table datatable="" 
  class="table" 
  dt-options="$ctrl.dtOptions" 
  dt-columns="$ctrl.dtColumns">
</table>`,
  controller: TableController
};
