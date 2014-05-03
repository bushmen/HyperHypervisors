function loadTemplate(template_name) {
    if (!loadTemplate.tmpl_cache) {
        loadTemplate.tmpl_cache = {};
    }
    if (!loadTemplate.tmpl_cache[template_name]) {
        var tmpl_url = 'resources/html/' + template_name + '.html';
        var tmpl_string;
        $.ajax({
            url: tmpl_url,
            method: 'GET',
            async: false,
            success: function (data) {
                tmpl_string = data;
            }
        });
        loadTemplate.tmpl_cache[template_name] = tmpl_string;
    }
    return loadTemplate.tmpl_cache[template_name];
}

$(document).ready(function () {
    $.ajax({
        'url': 'infrastructure/data',
        'success': function (data) {
            $('#servers-tree').jstree({
                'core': {
                    'data': data
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
            }).on('select_node.jstree', function (event, selectData) {
                var node = selectData.node;
                console.log(node);
                var template = _.template(loadTemplate(node.type));
                $('#data-table').html(template(node.original));
            });
        }
    });
});