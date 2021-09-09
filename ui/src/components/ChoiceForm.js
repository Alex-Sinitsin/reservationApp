import React, {useState, useRef, useEffect} from "react";
import "./ChoiceForm.css";
import Select from 'react-select'
import ItemService from "../services/item.service"


const ChooseItems = (props) => {

  const [itemID, setItemID] = useState(null);
  const [itemList, setItemList] = useState([]);

  const [whichItemID, setWhichItemID] = useState(null);

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

        setItemList(parsedList);
      } catch (err) {
        console.log(err, "API ERROR");
      }
    }

    getItemData();
  }, []);


  function handleChangeItem(selectedItem) {
    setItemID(selectedItem);
  }

  function handleChangeWhichItem(selectedItem) {
    setWhichItemID(selectedItem);
  }


  const whichItemsList = [
    {value: 'all', label: 'Все события'},
    {value: 'mine', label: 'Мои события'},
    {value: 'member', label: 'Участник события'},
    {value: 'other', label: 'Чужие события'},
  ]

  return (
    <div className='choiceForm px-4'>
      <div className="row">
        <div className="col">
          <label htmlFor="itemID">Помещение/Предмет:</label>
          <Select
              value={itemID}
              onChange={handleChangeItem}
              options={itemList}
              placeholder={null}
              components={{IndicatorSeparator: () => null}}
          />
        </div>

        <div className="col">
          <label htmlFor="itemID">Отображать:</label>
          <Select
              value={whichItemID}
              onChange={handleChangeWhichItem}
              options={whichItemsList}
              defaultValue={whichItemsList[0]}
              placeholder={null}
              components={{IndicatorSeparator: () => null}}
          />
        </div>
      </div>
    </div>
  );
}

export default ChooseItems;