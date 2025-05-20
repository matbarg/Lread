package com.example.lread.utils

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient

// custom WebClient to inject the js once the page is loaded
class ReaderWebViewClient(
    private val getJsStyles: () -> String,
    private val getCurrentAnchorId: () -> String
) : WebViewClient() {
    private var readerIsFirstOpened = true

    private val addIdsJs = """
            (function() {
            const paragraphs = document.getElementsByTagName("p");
                for (let i = 0; i < paragraphs.length; i++) {
                    paragraphs[i].id = "para-" + i;
                }
            })();
        """.trimIndent()

    private val addSpacing = """
        document.body.style.padding = '80px 16px 200px';
    """.trimIndent()

    private val addFontFamilies = """
        const fontFamilyStyle = document.createElement('style');
        fontFamilyStyle.textContent = `
            @font-face {
                font-family: 'LibreBaskerville';
                src: url('file:///android_asset/fonts/librebaskerville_regular.ttf');
            }
            @font-face {
                font-family: 'Lato';
                src: url('file:///android_asset/fonts/lato_regular.ttf');
            }
            @font-face {
                font-family: 'Sanchez';
                src: url('file:///android_asset/fonts/sanchez_regular.ttf');
            }
        `;
        document.head.appendChild(fontFamilyStyle);
    """.trimIndent()

    private val scrollToParagraphJs = """
            const el = document.getElementById('${getCurrentAnchorId()}');
            if (el) {
                el.scrollIntoView({ behavior: 'auto', block: 'start' });
            }
        """.trimIndent()

    /** Script Explanation:
     * Retrieves all p elements
     *
     * getCenterParagraphId() to find out which paragraph is currently viewed,
     * by checking if any paragraphs bounding size stretches across the center of the screen
     *
     * checkAndReport() to perform the check and update the state through the JavascriptInterface if all conditions are met
     *
     * checkAndReport() is added as a scroll event listener
     */
    private val reportAnchorJs = """
        (function() {
            const paragraphs = document.getElementsByTagName("p");
            
            let lastSentId = "";
            
            function getCenterParagraphId() {
                const centerY = window.scrollY + window.innerHeight / 2;
                
                for (let i = 0; i < paragraphs.length; i++) {
                    const p = paragraphs[i];
                    const rect = p.getBoundingClientRect();
                    const top = rect.top + window.scrollY;
                    const bottom = top + rect.height;
        
                    if (top <= centerY && bottom >= centerY) {
                        return p.id;
                    }
                }
                
                return "";
            }
            
            function checkAndReport() {
                const currentId = getCenterParagraphId();
                if (currentId && currentId != lastSentId) {
                    lastSentId = currentId;
                    if (window.ReaderBridge && window.ReaderBridge.reportVisibleAnchor) {
                        window.ReaderBridge.reportVisibleAnchor(currentId);
                    }
                }
            }
            
            window.addEventListener("scroll", () => {
                window.requestAnimationFrame(checkAndReport);
            });
            
            checkAndReport();
        })();
    """.trimIndent()

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        view?.evaluateJavascript(addIdsJs, null)

        view?.evaluateJavascript(addSpacing, null)

        view?.evaluateJavascript(addFontFamilies, null)

        view?.evaluateJavascript(getJsStyles(), null)

        if (readerIsFirstOpened) {
            view?.evaluateJavascript(scrollToParagraphJs, null)
            readerIsFirstOpened = false
        }

        view?.evaluateJavascript(reportAnchorJs, null)
    }
}