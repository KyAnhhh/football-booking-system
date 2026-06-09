import { useEffect, useState } from "react";
import { getFields } from "../../services/fieldService";
import { useNavigate } from "react-router-dom";
import { Button, Form } from "react-bootstrap";
import "../../assets/styles/FootballField.css";

export default function FootballField() {

  const [fields, setFields] = useState([]);

  const [keyword, setKeyword] = useState("");

  const [filters, setFilters] = useState({
    type: "",
    minPrice: "",
    maxPrice: "",
  });

  const navigate = useNavigate();

  // LOAD FIELDS
  const loadFields = async (
    customKeyword = keyword,
    customFilters = filters
  ) => {

    try {

      const params = {
        page: 0,
        size: 10,
      };

      // KEYWORD
      if (customKeyword?.trim()) {
        params.keyword = customKeyword.trim();
      }

      // TYPE
      if (customFilters.type) {
        params.type = customFilters.type;
      }

      // PRICE
      if (customFilters.minPrice) {
        params.minPrice = customFilters.minPrice;
      }

      if (customFilters.maxPrice) {
        params.maxPrice = customFilters.maxPrice;
      }

      console.log("PARAMS:", params);

      const data = await getFields(params);

      console.log("DATA:", data);

      setFields(data.content || []);

    } catch (error) {
      console.error(error);
    }
  };

  // LOAD FIRST
  useEffect(() => {
    loadFields();
  }, []);

  // AUTO SEARCH
  useEffect(() => {

    const debounce = setTimeout(() => {
      loadFields(keyword, filters);
    }, 400);

    return () => clearTimeout(debounce);

  }, [keyword, filters]);

  // PRICE FILTER
  const handlePriceFilter = (value) => {

    let newFilters = {
      ...filters,
      minPrice: "",
      maxPrice: "",
    };

    if (value === "UNDER_200") {
      newFilters.maxPrice = 200000;
    }

    if (value === "200_500") {
      newFilters.minPrice = 200000;
      newFilters.maxPrice = 500000;
    }

    if (value === "OVER_500") {
      newFilters.minPrice = 500000;
    }

    setFilters(newFilters);
  };

  // TYPE FILTER
  const handleTypeFilter = (value) => {

    const newFilters = {
      ...filters,
      type: value,
    };

    setFilters(newFilters);
  };

  return (
    <div className="field-page">

      <h2 className="title">Sân bóng</h2>

      <div className="field-layout">

        {/* SIDEBAR */}
        <div className="field-sidebar">

          <h4>Bộ lọc</h4>

          {/* SEARCH */}
          <Form.Group className="mb-4">

            <Form.Label>Tìm sân</Form.Label>

            <Form.Control
              type="text"
              placeholder="Nhập tên sân..."
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
            />

          </Form.Group>

          <h6>Loại sân</h6>
          <Button
            variant={filters.type === "" ? "primary" : "outline-dark"}
            onClick={() => handleTypeFilter("")}
          >
            Tất cả
          </Button>

          <Button
            variant={filters.type === "FIVE_A_SIDE" ? "primary" : "outline-dark"}
            onClick={() => handleTypeFilter("FIVE_A_SIDE")}
          >
            Sân 5
          </Button>

          <Button
            variant={filters.type === "SEVEN_A_SIDE" ? "primary" : "outline-dark"}
            onClick={() => handleTypeFilter("SEVEN_A_SIDE")}
          >
            Sân 7
          </Button>

          <Button
            variant={filters.type === "ELEVEN_A_SIDE" ? "primary" : "outline-dark"}
            onClick={() => handleTypeFilter("ELEVEN_A_SIDE")}
          >
            Sân 11
          </Button>

          <h6>Mức giá</h6>
          <Button
            variant={
              filters.maxPrice === 200000 &&
              !filters.minPrice
                ? "primary"
                : "outline-dark"
            }
            onClick={() => handlePriceFilter("UNDER_200")}
          >
            Dưới 200k
          </Button>

          <Button
            variant={
              filters.minPrice === 200000 &&
              filters.maxPrice === 500000
                ? "primary"
                : "outline-dark"
            }
            onClick={() => handlePriceFilter("200_500")}
          >
            200k - 500k
          </Button>

          <Button
            variant={
              filters.minPrice === 500000 &&
              !filters.maxPrice
                ? "primary"
                : "outline-dark"
            }
            onClick={() => handlePriceFilter("OVER_500")}
          >
            Trên 500k
          </Button>
        </div>

        {/* FIELD LIST */}
        <div className="field-grid">

          {fields.length > 0 ? (

            fields.map((f) => (

              <div key={f.id} className="field-card">

                <h3>{f.name}</h3>

                <img
                  src={f.imageUrl}
                  alt={f.name}
                />

                <p>{f.address}</p>

                <p className="field-price">
                  Giá: {f.pricePerHour} VND/h
                </p>

                <Button
                  className="btn-custom"
                  onClick={() =>
                    navigate(`/football-fields/${f.id}/detail`)
                  }
                >
                  Xem chi tiết
                </Button>

              </div>
            ))

          ) : (

            <div className="no-data">
              Không tìm thấy sân phù hợp
            </div>

          )}

        </div>
      </div>
    </div>
  );
}