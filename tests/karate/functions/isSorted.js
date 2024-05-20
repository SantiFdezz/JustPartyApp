isSorted = (response, params, data) => {
    if (params.mine == true && (params.orderBy !== undefined || params.province !== undefined)) {
        return false;
    }
    for (var i = 0; i < response.length; i++) {
        if (params.orderBy === 'date') {
            const date1 = new Date(response[i][params.orderBy]).getTime();
            const date2 = new Date(response[i + 1][params.orderBy]).getTime();
            if (date1 > date2) {
                return false;
            }
        } else if (params.orderBy === 'price') {
            if (response[i][params.orderBy] > response[i + 1][params.orderBy]) {
                return false;
            }
        } 

        if (params.mine == true) { 
            if (data.email !== response[i].manager) {
                return false;
            }
        }
    }
    return true;
}