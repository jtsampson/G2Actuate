package com.github.jtsampson.actuate

class DummyController {

    static responseFormats = ['json', 'xml']

    def index() {
        render "Nothing Special"
    }
}
