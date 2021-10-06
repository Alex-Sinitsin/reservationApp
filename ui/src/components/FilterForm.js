import React, {useEffect} from "react";
import "./FilterForm.css";
import Select from 'react-select'
import ItemService from "../services/item.service"


const FilterForm = (props) => {

  const {initialItemList, item, setItem, itemList, setItemList, whoseEvent ,whoseEventList, setWhoseEvent, getEventData} = props;

// Получение данных об Items в Select
  useEffect(() => {
    async function getItemData() {
      try {
        const response = await ItemService.getItems();
        const parsedList = response.data && response.data.map((item) => {

          return {
            value: item.id,
            label: `${item.name}`
          }
        })

        setItemList(list => [...list, ...parsedList]);
      } catch (err) {
        console.log(err, "API ERROR");
      }
    }

    getItemData();
  }, []);


  useEffect(() => getEventData(), [whoseEvent]);
  useEffect(() => getEventData(), [item]);

  function handleChangeItem(selectedItem) {
    setItem(selectedItem);
  }

  function handleChangeWhoseEvent(selectedEvent) {
    setWhoseEvent(selectedEvent);
  }



  return (
    <div className='choiceForm px-4'>
      <div className="row">
        <div className="col">
          <label htmlFor="itemID">Помещение/Предмет:</label>
          <Select
              value={item}
              defaultValue={initialItemList[0]}
              onChange={handleChangeItem}
              options={itemList}
              placeholder={null}
              components={{IndicatorSeparator: () => null}}
              isSearchable={false}
          />
        </div>

        <div className="col">
          <label htmlFor="itemID">Отображать:</label>
          <Select
              value={whoseEvent}
              defaultValue={whoseEventList[0]}
              onChange={handleChangeWhoseEvent}
              options={whoseEventList}
              placeholder={null}
              components={{IndicatorSeparator: () => null}}
              isSearchable={false}
          />
        </div>
      </div>
    </div>
  );
}

export default FilterForm;

