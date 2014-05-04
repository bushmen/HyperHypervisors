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
                        'valid_children': ['server']
                    },
                    'server': {
//                    'icon' : '/resources/img/server.png',
                        'valid_children': ['hypervisor']
                    },
                    'hypervisor': {
//                    'icon' : '/resources/img/hypervisor.png',
                        'valid_children': ['vm']
                    },
                    'vm': {
//                    'icon' : '/resources/img/vm.png',
                        'valid_children': ['appServer']
                    },
                    'appServer': {
//                    'icon' : '/resources/img/appServer.png',
//                        'valid_children': ['app']
//                    },
//                    'app': {
////                    'icon' : '/resources/img/app.png',
                        'valid_children': []
                    }
                },
                'sort': function (node1, node2) {
                    return this._model.data[node1].text.toLowerCase() <= this._model.data[node2].text.toLowerCase() ? -1 : 1;
                },
                'plugins': ['types', 'sort']
            }).on('select_node.jstree', function (event, selectData) {
                var node = selectData.node;
                var selected = selectData.selected;
                var jsTreeRef = $.jstree.reference('#servers-tree');
                for (var i in selected) {
                    if (selected[i] !== node.id) {
                        jsTreeRef.deselect_node(selected[i]);
                    }
                }

                var template = _.template(loadTemplate(node.type));
                $('#data-table').html(template(node.original));
            });
        }
    });
});

function addChild() {
    var selected = $.jstree.reference('#servers-tree').get_selected(true)[0];
    if (selected) {
        switch (selected.type) {
            case 'server':
            case 'hypervisor':
            case 'vm':
            case 'appServer':
                window.location.href = 'infrastructure/' + selected.type + '/' + selected.id + '/new-child';
                break;
        }
    }

    return false;
}

function modify() {
    var selected = $.jstree.reference('#servers-tree').get_selected(true)[0];
    if (selected) {
        switch (selected.type) {
            case 'server':
            case 'hypervisor':
            case 'vm':
            case 'appServer':
                window.location.href = 'infrastructure/' + selected.type + '/' + selected.id;
                break;
        }
    }

    return false;
}

function remove() {
    var selected = $.jstree.reference('#servers-tree').get_selected(true)[0];
    if (selected) {
        switch (selected.type) {
            case 'server':
            case 'hypervisor':
            case 'appServer':
                window.location.href = 'infrastructure/' + selected.type + '/' + selected.id;
                break;
        }
    }

    return false;
}