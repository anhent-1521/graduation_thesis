package com.example.tuananhe.myapplication.record.encode.glutils;


import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.example.tuananhe.myapplication.record.encode.glutils.ShaderConst.FRAGMENT_SHADER_SIMPLE;
import static com.example.tuananhe.myapplication.record.encode.glutils.ShaderConst.FRAGMENT_SHADER_SIMPLE_OES;
import static com.example.tuananhe.myapplication.record.encode.glutils.ShaderConst.GL_TEXTURE_2D;
import static com.example.tuananhe.myapplication.record.encode.glutils.ShaderConst.GL_TEXTURE_EXTERNAL_OES;
import static com.example.tuananhe.myapplication.record.encode.glutils.ShaderConst.VERTEX_SHADER;

/**
 * 描画領域全面にテクスチャを2D描画するためのヘルパークラス
 */
public class GLDrawer2D implements IDrawer2dES2 {
//	private static final boolean DEBUG = false; // FIXME set false on release
//	private static final String TAG = "GLDrawer2D";

    private static final float[] VERTICES = {1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f};
    private static final float[] TEXCOORD = {1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
    private static final int FLOAT_SZ = Float.SIZE / 8;

    private final int VERTEX_NUM;
    private final int VERTEX_SZ;
    private final FloatBuffer pVertex;
    private final FloatBuffer pTexCoord;
    private final int mTexTarget;
    private int hProgram;
    int maPositionLoc;
    int maTextureCoordLoc;
    int muMVPMatrixLoc;
    int muTexMatrixLoc;
    private final float[] mMvpMatrix = new float[16];

    /**
     * コンストラクタ
     * GLコンテキスト/EGLレンダリングコンテキストが有効な状態で呼ばないとダメ
     *
     * @param isOES 外部テクスチャ(GL_TEXTURE_EXTERNAL_OES)を使う場合はtrue。通常の2Dテキスチャならfalse
     */
    public GLDrawer2D(final boolean isOES) {
        this(VERTICES, TEXCOORD, isOES);
    }

    /**
     * コンストラクタ
     * GLコンテキスト/EGLレンダリングコンテキストが有効な状態で呼ばないとダメ
     *
     * @param vertices 頂点座標, floatを8個 = (x,y) x 4ペア
     * @param texcoord テクスチャ座標, floatを8個 = (s,t) x 4ペア
     * @param isOES    外部テクスチャ(GL_TEXTURE_EXTERNAL_OES)を使う場合はtrue。通常の2Dテキスチャならfalse
     */
    public GLDrawer2D(final float[] vertices, final float[] texcoord, final boolean isOES) {
        VERTEX_NUM = Math.min(vertices != null ? vertices.length : 0, texcoord != null ? texcoord.length : 0) / 2;
        VERTEX_SZ = VERTEX_NUM * 2;

        mTexTarget = isOES ? GL_TEXTURE_EXTERNAL_OES : GL_TEXTURE_2D;
        pVertex = ByteBuffer.allocateDirect(VERTEX_SZ * FLOAT_SZ)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        pVertex.put(vertices);
        pVertex.flip();
        pTexCoord = ByteBuffer.allocateDirect(VERTEX_SZ * FLOAT_SZ)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        pTexCoord.put(texcoord);
        pTexCoord.flip();

        if (isOES) {
            hProgram = GLHelper.loadShader(VERTEX_SHADER, FRAGMENT_SHADER_SIMPLE_OES);
        } else {
            hProgram = GLHelper.loadShader(VERTEX_SHADER, FRAGMENT_SHADER_SIMPLE);
        }
        // モデルビュー変換行列を初期化
        Matrix.setIdentityM(mMvpMatrix, 0);
        init();
    }

    /**
     * 破棄処理。GLコンテキスト/EGLレンダリングコンテキスト内で呼び出さないとダメ
     */
    @Override
    public void release() {
        if (hProgram >= 0) {
            GLES20.glDeleteProgram(hProgram);
        }
        hProgram = -1;
    }

    /**
     * 外部テクスチャを使うかどうか
     */
    public boolean isOES() {
        return mTexTarget == GL_TEXTURE_EXTERNAL_OES;
    }

    /**
     * モデルビュー変換行列を取得(内部配列を直接返すので変更時は要注意)
     */
    @Override
    public float[] getMvpMatrix() {
        return mMvpMatrix;
    }

    /**
     * モデルビュー変換行列に行列を割り当てる
     *
     * @param matrix 領域チェックしていないのでoffsetから16個以上必須
     */
    @Override
    public IDrawer2D setMvpMatrix(final float[] matrix, final int offset) {
        System.arraycopy(matrix, offset, mMvpMatrix, 0, 16);
        return this;
    }

    /**
     * モデルビュー変換行列のコピーを取得
     *
     * @param matrix 領域チェックしていないのでoffsetから16個以上必須
     */
    @Override
    public void getMvpMatrix(final float[] matrix, final int offset) {
        System.arraycopy(mMvpMatrix, 0, matrix, offset, 16);
    }

    /**
     * 指定したテクスチャを指定したテクスチャ変換行列を使って描画領域全面に描画するためのヘルパーメソッド
     * このクラスインスタンスのモデルビュー変換行列が設定されていればそれも適用された状態で描画する
     *
     * @param texId      texture ID
     * @param tex_matrix テクスチャ変換行列、nullならば以前に適用したものが再利用される.領域チェックしていないのでoffsetから16個以上確保しておくこと
     */
    @Override
    public synchronized void draw(final int texId, final float[] tex_matrix, final int offset) {
//		if (DEBUG) Log.v(TAG, "draw");
        if (hProgram < 0) return;
        GLES20.glUseProgram(hProgram);
        if (tex_matrix != null) {
            // テクスチャ変換行列が指定されている時
            GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, tex_matrix, offset);
        }
        // モデルビュー変換行列をセット
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mMvpMatrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(mTexTarget, texId);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_NUM);
        GLES20.glBindTexture(mTexTarget, 0);
        GLES20.glUseProgram(0);
    }

    /**
     * Textureオブジェクトを描画するためのヘルパーメソッド
     * Textureオブジェクトで管理しているテクスチャ名とテクスチャ座標変換行列を使って描画する
     */
    @Override
    public void draw(final ITexture texture) {
        draw(texture.getTexture(), texture.getTexMatrix(), 0);
    }

    /**
     * TextureOffscreenオブジェクトを描画するためのヘルパーメソッド
     */
    @Override
    public void draw(final TextureOffscreen offscreen) {
        draw(offscreen.getTexture(), offscreen.getTexMatrix(), 0);
    }

    /**
     * Helper methods for texture name generation
     * Just call GLHelper # initTex
     *
     * @return texture ID
     */
    public int initTex() {
        return GLHelper.initTex(mTexTarget, GLES20.GL_NEAREST);
    }

    /**
     * テクスチャ名破棄のヘルパーメソッド
     * GLHelper.deleteTexを呼び出すだけ
     */
    public void deleteTex(final int hTex) {
        GLHelper.deleteTex(hTex);
    }

    /**
     * 頂点シェーダー・フラグメントシェーダーを変更する
     * GLコンテキスト/EGLレンダリングコンテキスト内で呼び出さないとダメ
     * glUseProgramが呼ばれた状態で返る
     *
     * @param vs 頂点シェーダー文字列
     * @param fs フラグメントシェーダー文字列
     */
    public synchronized void updateShader(final String vs, final String fs) {
        release();
        hProgram = GLHelper.loadShader(vs, fs);
        init();
    }

    /**
     * フラグメントシェーダーを変更する
     * GLコンテキスト/EGLレンダリングコンテキスト内で呼び出さないとダメ
     * glUseProgramが呼ばれた状態で返る
     *
     * @param fs フラグメントシェーダー文字列
     */
    public void updateShader(final String fs) {
        updateShader(VERTEX_SHADER, fs);
    }

    /**
     * 頂点シェーダー・フラグメントシェーダーをデフォルトに戻す
     */
    public void resetShader() {
        release();
        if (isOES()) {
            hProgram = GLHelper.loadShader(VERTEX_SHADER, FRAGMENT_SHADER_SIMPLE_OES);
        } else {
            hProgram = GLHelper.loadShader(VERTEX_SHADER, FRAGMENT_SHADER_SIMPLE);
        }
        init();
    }

    /**
     * アトリビュート変数のロケーションを取得
     * glUseProgramが呼ばれた状態で返る
     */
    @Override
    public int glGetAttribLocation(final String name) {
        GLES20.glUseProgram(hProgram);
        return GLES20.glGetAttribLocation(hProgram, name);
    }

    /**
     * ユニフォーム変数のロケーションを取得
     * glUseProgramが呼ばれた状態で返る
     */
    @Override
    public int glGetUniformLocation(final String name) {
        GLES20.glUseProgram(hProgram);
        return GLES20.glGetUniformLocation(hProgram, name);
    }

    /**
     * glUseProgramが呼ばれた状態で返る
     */
    @Override
    public void glUseProgram() {
        GLES20.glUseProgram(hProgram);
    }

    /**
     * シェーダープログラム変更時の初期化処理
     * glUseProgramが呼ばれた状態で返る
     */
    private void init() {
        GLES20.glUseProgram(hProgram);
        maPositionLoc = GLES20.glGetAttribLocation(hProgram, "aPosition");
        maTextureCoordLoc = GLES20.glGetAttribLocation(hProgram, "aTextureCoord");
        muMVPMatrixLoc = GLES20.glGetUniformLocation(hProgram, "uMVPMatrix");
        muTexMatrixLoc = GLES20.glGetUniformLocation(hProgram, "uTexMatrix");
        //
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mMvpMatrix, 0);
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, mMvpMatrix, 0);
        GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, VERTEX_SZ, pVertex);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, VERTEX_SZ, pTexCoord);
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
    }

    public ByteBuffer getByteBuffer(int width, int height) {
        int size = width * height;
        ByteBuffer bb = ByteBuffer.allocateDirect(size * 4);
        bb.order(ByteOrder.nativeOrder());
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, bb);

        return bb;
    }
}
