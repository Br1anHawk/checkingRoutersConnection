import com.sun.xml.internal.fastinfoset.util.StringArray
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream
import javax.swing.table.DefaultTableModel

class MainLogicSolution {
    private val mainHostsInfo: ArrayList<ArrayList<String>> = arrayListOf()
    private var hostColumnNumber = -1
    private var statusColumnNumber = -1

    private val connectionChecker = ConnectionChecker()

    private var _tableModel: DefaultTableModel? = null
    private val tableModel get() = _tableModel!!

    fun setTableModel(tableModel: DefaultTableModel) {
        _tableModel = tableModel
    }

    fun getHostsInfo(): Array<Array<String>> {
        val arrayList = arrayListOf<Array<String>>()
        mainHostsInfo.forEach { arrayList.add(it.toTypedArray()) }
        return arrayList.toTypedArray()
    }

    fun loadInfo(file: File) {
        val fileInputStream = FileInputStream(file)
        val workbook = WorkbookFactory.create(fileInputStream)
        val sheet = workbook.getSheetAt(0);
        fileInputStream.close()

        var contentSheetLineNumberPosition = 0
        while (sheet.getRow(contentSheetLineNumberPosition) == null) contentSheetLineNumberPosition++
        val titleLineColumns: ArrayList<String> = arrayListOf()
        var sheetColumnNumberPosition = 0
        while (sheet.getRow(contentSheetLineNumberPosition).getCell(sheetColumnNumberPosition) != null) {
            titleLineColumns.add(sheet.getRow(contentSheetLineNumberPosition).getCell(sheetColumnNumberPosition).stringCellValue)
            sheetColumnNumberPosition++
        }
        for (index in titleLineColumns.indices) {
            if (titleLineColumns[index] == HOST_COLUMN_NAME) {
                hostColumnNumber = index
                break
            }
        }
        contentSheetLineNumberPosition++
        val columnsCount = titleLineColumns.size
        while (sheet.getRow(contentSheetLineNumberPosition) != null) {
            val lineInfo: ArrayList<String> = arrayListOf()
            for (index in 0 until columnsCount) {
                lineInfo.add(sheet.getRow(contentSheetLineNumberPosition).getCell(index).stringCellValue)
            }
            lineInfo.add("") //FOR STATUS INFO
            statusColumnNumber = columnsCount
            mainHostsInfo.add(lineInfo)
            contentSheetLineNumberPosition++
        }
        initTableModel(titleLineColumns)
    }

    private fun initTableModel(titleLineColumns: ArrayList<String>) {
        titleLineColumns.forEach {
            tableModel.addColumn(it)
        }
        tableModel.addColumn("Status")
        mainHostsInfo.forEach {
            tableModel.addRow(it.toArray())
        }
    }

    fun checkAllHostsConnection() {
        if (hostColumnNumber == -1) return
        val hosts = arrayListOf<String>()
        mainHostsInfo.forEach {
            hosts.add(it[hostColumnNumber])
        }
        connectionChecker.loadHosts(hosts)
        connectionChecker.checkAllHostsConnection()
        val routers = connectionChecker.getRouters()
        routers.forEach {
            updateRouterStatus(it)
        }
        updateTableModel()
    }

    private fun updateTableModel() {
        while (tableModel.rowCount > 0) {
            tableModel.removeRow(0)
        }
        mainHostsInfo.forEach {
            tableModel.addRow(it.toArray())
        }
    }

    private fun updateRouterStatus(router: Router) {
        mainHostsInfo.find {
            it[hostColumnNumber] == router.host
        }?.set(statusColumnNumber, statusConverterToString(router.status))
    }


    companion object {
        const val HOST_COLUMN_NAME = "host"
    }
}