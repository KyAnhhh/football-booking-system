import { useMemo, useState } from "react";
import { Button, Form, Table } from "react-bootstrap";
import swal from "sweetalert";
import { BsCloudArrowUp, BsDownload, BsFileEarmarkSpreadsheet } from "react-icons/bs";

import "../../assets/styles/ImportPage.css";
import { IMPORT_TYPES, downloadImportTemplate, importCsv } from "../../services/importService";

export default function ImportPage() {
  const [selectedType, setSelectedType] = useState(IMPORT_TYPES[0].value);
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);

  const currentType = useMemo(
    () => IMPORT_TYPES.find((type) => type.value === selectedType) || IMPORT_TYPES[0],
    [selectedType]
  );

  const handleDownloadTemplate = async () => {
    try {
      await downloadImportTemplate(selectedType);
    } catch (err) {
      swal("Lỗi", err.message, "error");
    }
  };

  const handleFileChange = (event) => {
    const selectedFile = event.target.files?.[0] || null;
    setFile(selectedFile);
    setResult(null);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!file) {
      swal("Thiếu file", "Vui lòng chọn file CSV trước khi import.", "warning");
      return;
    }

    if (!file.name.toLowerCase().endsWith(".csv")) {
      swal("Sai định dạng", "Hiện tại chỉ hỗ trợ file .csv.", "warning");
      return;
    }

    try {
      setLoading(true);
      const importResult = await importCsv(selectedType, file);
      setResult(importResult);
      swal("Hoàn tất", "Import dữ liệu đã xử lý xong.", "success");
    } catch (err) {
      swal("Lỗi", err.message, "error");
    } finally {
      setLoading(false);
    }
  };

  const renderIssues = (issues, emptyText) => {
    if (!issues || issues.length === 0) {
      return <p className="import-empty">{emptyText}</p>;
    }

    return (
      <Table responsive hover className="import-issue-table">
        <thead>
          <tr>
            <th>Dòng</th>
            <th>Cột</th>
            <th>Giá trị</th>
            <th>Nội dung</th>
          </tr>
        </thead>
        <tbody>
          {issues.map((issue, index) => (
            <tr key={`${issue.rowNumber}-${issue.field}-${index}`}>
              <td>{issue.rowNumber}</td>
              <td>{issue.field}</td>
              <td>{issue.value || "-"}</td>
              <td>{issue.message}</td>
            </tr>
          ))}
        </tbody>
      </Table>
    );
  };

  return (
    <div className="import-page">
      <div className="import-header">
        <div>
          <span className="import-kicker">Nhập dữ liệu hàng loạt</span>
          <h2>Import dữ liệu</h2>
          <p>
            Tải file mẫu, điền dữ liệu theo đúng cột rồi upload để tạo nhiều bản ghi cùng lúc.
          </p>
        </div>

        <Button variant="outline-primary" onClick={handleDownloadTemplate}>
          <BsDownload />
          Tải file mẫu
        </Button>
      </div>

      <div className="import-layout">
        <section className="import-panel">
          <div className="import-type-list" role="list">
            {IMPORT_TYPES.map((type) => (
              <button
                key={type.value}
                type="button"
                className={`import-type-card ${selectedType === type.value ? "active" : ""}`}
                onClick={() => {
                  setSelectedType(type.value);
                  setResult(null);
                }}
              >
                <BsFileEarmarkSpreadsheet />
                <span>{type.label}</span>
                <small>{type.description}</small>
              </button>
            ))}
          </div>

          <Form className="import-form" onSubmit={handleSubmit}>
            <Form.Group>
              <Form.Label>Loại dữ liệu</Form.Label>
              <Form.Select
                value={selectedType}
                onChange={(event) => {
                  setSelectedType(event.target.value);
                  setResult(null);
                }}
              >
                {IMPORT_TYPES.map((type) => (
                  <option key={type.value} value={type.value}>
                    {type.label}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Form.Group>
              <Form.Label>File CSV</Form.Label>
              <Form.Control type="file" accept=".csv,text/csv" onChange={handleFileChange} />
              <Form.Text>
                Đang chọn: {currentType.label}. File cần dùng đúng header trong file mẫu.
              </Form.Text>
            </Form.Group>

            <Button type="submit" disabled={loading}>
              <BsCloudArrowUp />
              {loading ? "Đang import..." : "Import dữ liệu"}
            </Button>
          </Form>
        </section>

        <section className="import-guide">
          <h3>Thứ tự import gợi ý</h3>
          <ol>
            <li>Import size nếu hệ thống chưa có size.</li>
            <li>Import sản phẩm với categoryId và brandId đã tồn tại.</li>
            <li>Import biến thể sản phẩm để gán giá bán và tồn kho.</li>
            <li>Import sân bóng nếu cần tạo nhiều sân cùng lúc.</li>
            <li>Import ảnh sản phẩm và ảnh sân bóng bằng URL.</li>
            <li>Import bảng giá sân, ngày lễ và giá ngày lễ.</li>
            <li>Import dịch vụ thêm nếu sân có bán nước, thuê bóng hoặc áo bib.</li>
            <li>Import nhà cung cấp trước khi import phiếu nhập kho.</li>
            <li>Import item kho rồi mới import số lượng bằng phiếu nhập kho.</li>
          </ol>
          <p>
            Nếu chỉ import sản phẩm mà chưa import biến thể, sản phẩm sẽ chưa có giá và tồn kho để bán.
            Bảng giá sân sẽ bị từ chối nếu khung giờ bị overlap với rule đang hoạt động.
          </p>
        </section>
      </div>

      {result && (
        <section className="import-result">
          <div className="import-summary-grid">
            <div>
              <span>Tổng dòng</span>
              <strong>{result.totalRows}</strong>
            </div>
            <div>
              <span>Thành công</span>
              <strong>{result.successRows}</strong>
            </div>
            <div>
              <span>Lỗi</span>
              <strong>{result.failedRows}</strong>
            </div>
            <div>
              <span>Cảnh báo</span>
              <strong>{result.warningRows}</strong>
            </div>
          </div>

          <div className="import-result-section">
            <h3>Lỗi cần sửa</h3>
            {renderIssues(result.errors, "Không có lỗi trong file import.")}
          </div>

          <div className="import-result-section">
            <h3>Cảnh báo</h3>
            {renderIssues(result.warnings, "Không có cảnh báo.")}
          </div>
        </section>
      )}
    </div>
  );
}
