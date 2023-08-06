console.log("hello ");

const toggleSidebar=()=>{
    if ($(".sidebar").is(":visible")) {
        //true

        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    }
    else{

        //false
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
};

// search

const search=()=>{
    console.log("Searching....");

    let query = $("#search-input").val();
    // console.log(query);

    if(query == ""){
        $(".search-result").hide();
    }else{
        //search
        console.log(query);

        //sending request to server
        let url =  `http://localhost:1234/search/${query}`;

        fetch(url)
            .then((response) =>{
                return response.json();
            }).then((data) => {
                //data.......
                console.log(data);

                let text = `<div class='list-group'>`;

                data.forEach((contact) =>{
                    text += `<a href='/user/${contact.cid}/contact' class='list-group-item list-group-item-action'>${contact.name}  </a>`
                });


                text +=`</div>`;
                
                $(".search-result").html(text);
                $(".search-result").show();
            });
        
    }
};



