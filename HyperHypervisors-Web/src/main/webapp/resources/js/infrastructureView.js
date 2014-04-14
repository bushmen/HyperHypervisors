$('#servers-tree').jstree({
    'core': {
//                'url' : '/infrastructure/servers',
        'data': [
            {
                text: "Server 1",
                type: "server",
                children: [
                    {
                        text: "Hipervisor 1",
                        type: "hipervisor",
                        children: [
                            {
                                text: "Virtual machine 1",
                                type: "vm",
                                children: [
                                    {
                                        text: "App server 1",
                                        type: "appServer",
                                        children: [
                                            {
                                                text: "App 1",
                                                type: "app"
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        text: "Hipervisor 2",
                        type: "hipervisor",
                        children: [

                        ]
                    }
                ]
            },
            {
                text: "Server 2",
                type: "server",
                children: [
                    {
                        text: "Hipervisor 3",
                        type: "hipervisor",
                        children: [
                            {
                                text: "Virtual machine 2",
                                type: "vm",
                                children: [
                                    {
                                        text: "App server 2",
                                        type: "appServer",
                                        children: [
                                            {
                                                text: "App 2",
                                                type: "app"
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        ]/*function (node) {
         return { 'id' : node.id };
         }*/
    },
    'types': {
        '#': {
            "valid_children": ["server"]
        },
        'server': {
//                    "icon" : "/resources/img/server.png",
            "valid_children": ["hypervisor"]
        },
        'hypervisor': {
//                    "icon" : "/resources/img/hypervisor.png",
            "valid_children": ["vm"]
        },
        'vm': {
//                    "icon" : "/resources/img/vm.png",
            "valid_children": ["appServer"]
        },
        'appServer': {
//                    "icon" : "/resources/img/appServer.png",
            "valid_children": ["app"]
        },
        'app': {
//                    "icon" : "/resources/img/app.png",
            "valid_children": []
        }
    },
    'plugins': ["types"]
});