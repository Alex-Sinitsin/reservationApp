import React, {useState, useEffect} from "react";
import "./FilterForm.css";
import Select from 'react-select'
import ItemService from "../services/item.service"


const FilterEvents = () => {

  const initialItemList = [{value: 0, label: 'Все'}]
  const [itemList, setItemList] = useState(initialItemList);
  const [item, setItem] = useState();

  const whoseItemsList = [
    {value: 'all', label: 'Все события'},
    {value: 'mine', label: 'Созданные события'},
    {value: 'member', label: 'Мои события'},
    {value: 'other', label: 'Чужие события'},
  ]
  const [whoseItem, setWhoseItem] = useState();


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


  function handleChangeItem(selectedItem) {
    setItem(selectedItem);
  }

  function handleChangeWhoseItem(selectedItem) {
    setWhoseItem(selectedItem);
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
              value={whoseItem}
              defaultValue={whoseItemsList[0]}
              onChange={handleChangeWhoseItem}
              options={whoseItemsList}
              placeholder={null}
              components={{IndicatorSeparator: () => null}}
              isSearchable={false}
          />
        </div>
      </div>
    </div>
  );
}

export default FilterEvents;