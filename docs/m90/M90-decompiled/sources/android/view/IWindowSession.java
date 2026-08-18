package android.view;

import android.content.ClipData;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.IWindow;
import android.view.IWindowId;
import android.view.WindowManager;

/* JADX INFO: loaded from: classes.dex */
public interface IWindowSession extends IInterface {
    int add(IWindow iWindow, int i, WindowManager.LayoutParams layoutParams, int i2, Rect rect, InputChannel inputChannel) throws RemoteException;

    int addToDisplay(IWindow iWindow, int i, WindowManager.LayoutParams layoutParams, int i2, int i3, Rect rect, InputChannel inputChannel) throws RemoteException;

    int addToDisplayWithoutInputChannel(IWindow iWindow, int i, WindowManager.LayoutParams layoutParams, int i2, int i3, Rect rect) throws RemoteException;

    int addWithoutInputChannel(IWindow iWindow, int i, WindowManager.LayoutParams layoutParams, int i2, Rect rect) throws RemoteException;

    void dragRecipientEntered(IWindow iWindow) throws RemoteException;

    void dragRecipientExited(IWindow iWindow) throws RemoteException;

    void finishDrawing(IWindow iWindow) throws RemoteException;

    void getDisplayFrame(IWindow iWindow, Rect rect) throws RemoteException;

    boolean getInTouchMode() throws RemoteException;

    IWindowId getWindowId(IBinder iBinder) throws RemoteException;

    void onRectangleOnScreenRequested(IBinder iBinder, Rect rect, boolean z) throws RemoteException;

    boolean outOfMemory(IWindow iWindow) throws RemoteException;

    void performDeferredDestroy(IWindow iWindow) throws RemoteException;

    boolean performDrag(IWindow iWindow, IBinder iBinder, float f, float f2, float f3, float f4, ClipData clipData) throws RemoteException;

    boolean performHapticFeedback(IWindow iWindow, int i, boolean z) throws RemoteException;

    IBinder prepareDrag(IWindow iWindow, int i, int i2, int i3, Surface surface) throws RemoteException;

    int relayout(IWindow iWindow, int i, WindowManager.LayoutParams layoutParams, int i2, int i3, int i4, int i5, Rect rect, Rect rect2, Rect rect3, Rect rect4, Configuration configuration, Surface surface) throws RemoteException;

    void remove(IWindow iWindow) throws RemoteException;

    void reportDropResult(IWindow iWindow, boolean z) throws RemoteException;

    Bundle sendWallpaperCommand(IBinder iBinder, String str, int i, int i2, int i3, Bundle bundle, boolean z) throws RemoteException;

    void setInTouchMode(boolean z) throws RemoteException;

    void setInsets(IWindow iWindow, int i, Rect rect, Rect rect2, Region region) throws RemoteException;

    void setTransparentRegion(IWindow iWindow, Region region) throws RemoteException;

    void setUniverseTransform(IBinder iBinder, float f, float f2, float f3, float f4, float f5, float f6, float f7) throws RemoteException;

    void setWallpaperPosition(IBinder iBinder, float f, float f2, float f3, float f4) throws RemoteException;

    void wallpaperCommandComplete(IBinder iBinder, Bundle bundle) throws RemoteException;

    void wallpaperOffsetsComplete(IBinder iBinder) throws RemoteException;

    public static abstract class Stub extends Binder implements IWindowSession {
        private static final String DESCRIPTOR = "android.view.IWindowSession";
        static final int TRANSACTION_add = 1;
        static final int TRANSACTION_addToDisplay = 2;
        static final int TRANSACTION_addToDisplayWithoutInputChannel = 4;
        static final int TRANSACTION_addWithoutInputChannel = 3;
        static final int TRANSACTION_dragRecipientEntered = 19;
        static final int TRANSACTION_dragRecipientExited = 20;
        static final int TRANSACTION_finishDrawing = 12;
        static final int TRANSACTION_getDisplayFrame = 11;
        static final int TRANSACTION_getInTouchMode = 14;
        static final int TRANSACTION_getWindowId = 27;
        static final int TRANSACTION_onRectangleOnScreenRequested = 26;
        static final int TRANSACTION_outOfMemory = 8;
        static final int TRANSACTION_performDeferredDestroy = 7;
        static final int TRANSACTION_performDrag = 17;
        static final int TRANSACTION_performHapticFeedback = 15;
        static final int TRANSACTION_prepareDrag = 16;
        static final int TRANSACTION_relayout = 6;
        static final int TRANSACTION_remove = 5;
        static final int TRANSACTION_reportDropResult = 18;
        static final int TRANSACTION_sendWallpaperCommand = 23;
        static final int TRANSACTION_setInTouchMode = 13;
        static final int TRANSACTION_setInsets = 10;
        static final int TRANSACTION_setTransparentRegion = 9;
        static final int TRANSACTION_setUniverseTransform = 25;
        static final int TRANSACTION_setWallpaperPosition = 21;
        static final int TRANSACTION_wallpaperCommandComplete = 24;
        static final int TRANSACTION_wallpaperOffsetsComplete = 22;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWindowSession asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IWindowSession)) {
                return (IWindowSession) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    IWindow iWindowAsInterface = IWindow.Stub.asInterface(parcel.readStrongBinder());
                    int i3 = parcel.readInt();
                    WindowManager.LayoutParams layoutParamsCreateFromParcel = parcel.readInt() != 0 ? WindowManager.LayoutParams.CREATOR.createFromParcel(parcel) : null;
                    int i4 = parcel.readInt();
                    Rect rect = new Rect();
                    InputChannel inputChannel = new InputChannel();
                    int iAdd = add(iWindowAsInterface, i3, layoutParamsCreateFromParcel, i4, rect, inputChannel);
                    parcel2.writeNoException();
                    parcel2.writeInt(iAdd);
                    parcel2.writeInt(1);
                    rect.writeToParcel(parcel2, 1);
                    parcel2.writeInt(1);
                    inputChannel.writeToParcel(parcel2, 1);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    IWindow iWindowAsInterface2 = IWindow.Stub.asInterface(parcel.readStrongBinder());
                    int i5 = parcel.readInt();
                    WindowManager.LayoutParams layoutParamsCreateFromParcel2 = parcel.readInt() != 0 ? WindowManager.LayoutParams.CREATOR.createFromParcel(parcel) : null;
                    int i6 = parcel.readInt();
                    int i7 = parcel.readInt();
                    Rect rect2 = new Rect();
                    InputChannel inputChannel2 = new InputChannel();
                    int iAddToDisplay = addToDisplay(iWindowAsInterface2, i5, layoutParamsCreateFromParcel2, i6, i7, rect2, inputChannel2);
                    parcel2.writeNoException();
                    parcel2.writeInt(iAddToDisplay);
                    parcel2.writeInt(1);
                    rect2.writeToParcel(parcel2, 1);
                    parcel2.writeInt(1);
                    inputChannel2.writeToParcel(parcel2, 1);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    IWindow iWindowAsInterface3 = IWindow.Stub.asInterface(parcel.readStrongBinder());
                    int i8 = parcel.readInt();
                    WindowManager.LayoutParams layoutParamsCreateFromParcel3 = parcel.readInt() != 0 ? WindowManager.LayoutParams.CREATOR.createFromParcel(parcel) : null;
                    int i9 = parcel.readInt();
                    Rect rect3 = new Rect();
                    int iAddWithoutInputChannel = addWithoutInputChannel(iWindowAsInterface3, i8, layoutParamsCreateFromParcel3, i9, rect3);
                    parcel2.writeNoException();
                    parcel2.writeInt(iAddWithoutInputChannel);
                    parcel2.writeInt(1);
                    rect3.writeToParcel(parcel2, 1);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    IWindow iWindowAsInterface4 = IWindow.Stub.asInterface(parcel.readStrongBinder());
                    int i10 = parcel.readInt();
                    WindowManager.LayoutParams layoutParamsCreateFromParcel4 = parcel.readInt() != 0 ? WindowManager.LayoutParams.CREATOR.createFromParcel(parcel) : null;
                    int i11 = parcel.readInt();
                    int i12 = parcel.readInt();
                    Rect rect4 = new Rect();
                    int iAddToDisplayWithoutInputChannel = addToDisplayWithoutInputChannel(iWindowAsInterface4, i10, layoutParamsCreateFromParcel4, i11, i12, rect4);
                    parcel2.writeNoException();
                    parcel2.writeInt(iAddToDisplayWithoutInputChannel);
                    parcel2.writeInt(1);
                    rect4.writeToParcel(parcel2, 1);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    remove(IWindow.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    IWindow iWindowAsInterface5 = IWindow.Stub.asInterface(parcel.readStrongBinder());
                    int i13 = parcel.readInt();
                    WindowManager.LayoutParams layoutParamsCreateFromParcel5 = parcel.readInt() != 0 ? WindowManager.LayoutParams.CREATOR.createFromParcel(parcel) : null;
                    int i14 = parcel.readInt();
                    int i15 = parcel.readInt();
                    int i16 = parcel.readInt();
                    int i17 = parcel.readInt();
                    Rect rect5 = new Rect();
                    Rect rect6 = new Rect();
                    Rect rect7 = new Rect();
                    Rect rect8 = new Rect();
                    Configuration configuration = new Configuration();
                    Surface surface = new Surface();
                    int iRelayout = relayout(iWindowAsInterface5, i13, layoutParamsCreateFromParcel5, i14, i15, i16, i17, rect5, rect6, rect7, rect8, configuration, surface);
                    parcel2.writeNoException();
                    parcel2.writeInt(iRelayout);
                    parcel2.writeInt(1);
                    rect5.writeToParcel(parcel2, 1);
                    parcel2.writeInt(1);
                    rect6.writeToParcel(parcel2, 1);
                    parcel2.writeInt(1);
                    rect7.writeToParcel(parcel2, 1);
                    parcel2.writeInt(1);
                    rect8.writeToParcel(parcel2, 1);
                    parcel2.writeInt(1);
                    configuration.writeToParcel(parcel2, 1);
                    parcel2.writeInt(1);
                    surface.writeToParcel(parcel2, 1);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    performDeferredDestroy(IWindow.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zOutOfMemory = outOfMemory(IWindow.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(zOutOfMemory ? 1 : 0);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    setTransparentRegion(IWindow.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt() != 0 ? Region.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    setInsets(IWindow.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt(), parcel.readInt() != 0 ? Rect.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? Rect.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? Region.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    IWindow iWindowAsInterface6 = IWindow.Stub.asInterface(parcel.readStrongBinder());
                    Rect rect9 = new Rect();
                    getDisplayFrame(iWindowAsInterface6, rect9);
                    parcel2.writeNoException();
                    parcel2.writeInt(1);
                    rect9.writeToParcel(parcel2, 1);
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    finishDrawing(IWindow.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    setInTouchMode(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 14:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean inTouchMode = getInTouchMode();
                    parcel2.writeNoException();
                    parcel2.writeInt(inTouchMode ? 1 : 0);
                    return true;
                case 15:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zPerformHapticFeedback = performHapticFeedback(IWindow.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(zPerformHapticFeedback ? 1 : 0);
                    return true;
                case 16:
                    parcel.enforceInterface(DESCRIPTOR);
                    IWindow iWindowAsInterface7 = IWindow.Stub.asInterface(parcel.readStrongBinder());
                    int i18 = parcel.readInt();
                    int i19 = parcel.readInt();
                    int i20 = parcel.readInt();
                    Surface surface2 = new Surface();
                    IBinder iBinderPrepareDrag = prepareDrag(iWindowAsInterface7, i18, i19, i20, surface2);
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(iBinderPrepareDrag);
                    parcel2.writeInt(1);
                    surface2.writeToParcel(parcel2, 1);
                    return true;
                case 17:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zPerformDrag = performDrag(IWindow.Stub.asInterface(parcel.readStrongBinder()), parcel.readStrongBinder(), parcel.readFloat(), parcel.readFloat(), parcel.readFloat(), parcel.readFloat(), parcel.readInt() != 0 ? ClipData.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(zPerformDrag ? 1 : 0);
                    return true;
                case 18:
                    parcel.enforceInterface(DESCRIPTOR);
                    reportDropResult(IWindow.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 19:
                    parcel.enforceInterface(DESCRIPTOR);
                    dragRecipientEntered(IWindow.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 20:
                    parcel.enforceInterface(DESCRIPTOR);
                    dragRecipientExited(IWindow.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 21:
                    parcel.enforceInterface(DESCRIPTOR);
                    setWallpaperPosition(parcel.readStrongBinder(), parcel.readFloat(), parcel.readFloat(), parcel.readFloat(), parcel.readFloat());
                    parcel2.writeNoException();
                    return true;
                case 22:
                    parcel.enforceInterface(DESCRIPTOR);
                    wallpaperOffsetsComplete(parcel.readStrongBinder());
                    parcel2.writeNoException();
                    return true;
                case 23:
                    parcel.enforceInterface(DESCRIPTOR);
                    Bundle bundleSendWallpaperCommand = sendWallpaperCommand(parcel.readStrongBinder(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? Bundle.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0);
                    parcel2.writeNoException();
                    if (bundleSendWallpaperCommand != null) {
                        parcel2.writeInt(1);
                        bundleSendWallpaperCommand.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 24:
                    parcel.enforceInterface(DESCRIPTOR);
                    wallpaperCommandComplete(parcel.readStrongBinder(), parcel.readInt() != 0 ? Bundle.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 25:
                    parcel.enforceInterface(DESCRIPTOR);
                    setUniverseTransform(parcel.readStrongBinder(), parcel.readFloat(), parcel.readFloat(), parcel.readFloat(), parcel.readFloat(), parcel.readFloat(), parcel.readFloat(), parcel.readFloat());
                    parcel2.writeNoException();
                    return true;
                case 26:
                    parcel.enforceInterface(DESCRIPTOR);
                    onRectangleOnScreenRequested(parcel.readStrongBinder(), parcel.readInt() != 0 ? Rect.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 27:
                    parcel.enforceInterface(DESCRIPTOR);
                    IWindowId windowId = getWindowId(parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(windowId != null ? windowId.asBinder() : null);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements IWindowSession {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.view.IWindowSession
            public int add(IWindow iWindow, int i, WindowManager.LayoutParams layoutParams, int i2, Rect rect, InputChannel inputChannel) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    parcelObtain.writeInt(i);
                    if (layoutParams != null) {
                        parcelObtain.writeInt(1);
                        layoutParams.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    int i3 = parcelObtain2.readInt();
                    if (parcelObtain2.readInt() != 0) {
                        rect.readFromParcel(parcelObtain2);
                    }
                    if (parcelObtain2.readInt() != 0) {
                        inputChannel.readFromParcel(parcelObtain2);
                    }
                    return i3;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public int addToDisplay(IWindow iWindow, int i, WindowManager.LayoutParams layoutParams, int i2, int i3, Rect rect, InputChannel inputChannel) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    parcelObtain.writeInt(i);
                    if (layoutParams != null) {
                        parcelObtain.writeInt(1);
                        layoutParams.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    int i4 = parcelObtain2.readInt();
                    if (parcelObtain2.readInt() != 0) {
                        rect.readFromParcel(parcelObtain2);
                    }
                    if (parcelObtain2.readInt() != 0) {
                        inputChannel.readFromParcel(parcelObtain2);
                    }
                    return i4;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public int addWithoutInputChannel(IWindow iWindow, int i, WindowManager.LayoutParams layoutParams, int i2, Rect rect) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    parcelObtain.writeInt(i);
                    if (layoutParams != null) {
                        parcelObtain.writeInt(1);
                        layoutParams.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    int i3 = parcelObtain2.readInt();
                    if (parcelObtain2.readInt() != 0) {
                        rect.readFromParcel(parcelObtain2);
                    }
                    return i3;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public int addToDisplayWithoutInputChannel(IWindow iWindow, int i, WindowManager.LayoutParams layoutParams, int i2, int i3, Rect rect) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    parcelObtain.writeInt(i);
                    if (layoutParams != null) {
                        parcelObtain.writeInt(1);
                        layoutParams.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    int i4 = parcelObtain2.readInt();
                    if (parcelObtain2.readInt() != 0) {
                        rect.readFromParcel(parcelObtain2);
                    }
                    return i4;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void remove(IWindow iWindow) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public int relayout(IWindow iWindow, int i, WindowManager.LayoutParams layoutParams, int i2, int i3, int i4, int i5, Rect rect, Rect rect2, Rect rect3, Rect rect4, Configuration configuration, Surface surface) throws Throwable {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    parcelObtain.writeInt(i);
                    if (layoutParams != null) {
                        parcelObtain.writeInt(1);
                        layoutParams.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeInt(i4);
                    parcelObtain.writeInt(i5);
                    try {
                        this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                        parcelObtain2.readException();
                        int i6 = parcelObtain2.readInt();
                        if (parcelObtain2.readInt() != 0) {
                            rect.readFromParcel(parcelObtain2);
                        }
                        if (parcelObtain2.readInt() != 0) {
                            rect2.readFromParcel(parcelObtain2);
                        }
                        if (parcelObtain2.readInt() != 0) {
                            rect3.readFromParcel(parcelObtain2);
                        }
                        if (parcelObtain2.readInt() != 0) {
                            rect4.readFromParcel(parcelObtain2);
                        }
                        if (parcelObtain2.readInt() != 0) {
                            configuration.readFromParcel(parcelObtain2);
                        }
                        if (parcelObtain2.readInt() != 0) {
                            surface.readFromParcel(parcelObtain2);
                        }
                        parcelObtain2.recycle();
                        parcelObtain.recycle();
                        return i6;
                    } catch (Throwable th) {
                        th = th;
                        parcelObtain2.recycle();
                        parcelObtain.recycle();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            }

            @Override // android.view.IWindowSession
            public void performDeferredDestroy(IWindow iWindow) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public boolean outOfMemory(IWindow iWindow) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    this.mRemote.transact(8, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void setTransparentRegion(IWindow iWindow, Region region) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    if (region != null) {
                        parcelObtain.writeInt(1);
                        region.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(9, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void setInsets(IWindow iWindow, int i, Rect rect, Rect rect2, Region region) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    parcelObtain.writeInt(i);
                    if (rect != null) {
                        parcelObtain.writeInt(1);
                        rect.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (rect2 != null) {
                        parcelObtain.writeInt(1);
                        rect2.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (region != null) {
                        parcelObtain.writeInt(1);
                        region.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(10, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void getDisplayFrame(IWindow iWindow, Rect rect) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    this.mRemote.transact(11, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    if (parcelObtain2.readInt() != 0) {
                        rect.readFromParcel(parcelObtain2);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void finishDrawing(IWindow iWindow) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    this.mRemote.transact(12, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void setInTouchMode(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(13, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public boolean getInTouchMode() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public boolean performHapticFeedback(IWindow iWindow, int i, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(15, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public IBinder prepareDrag(IWindow iWindow, int i, int i2, int i3, Surface surface) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    this.mRemote.transact(16, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    IBinder strongBinder = parcelObtain2.readStrongBinder();
                    if (parcelObtain2.readInt() != 0) {
                        surface.readFromParcel(parcelObtain2);
                    }
                    return strongBinder;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public boolean performDrag(IWindow iWindow, IBinder iBinder, float f, float f2, float f3, float f4, ClipData clipData) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeFloat(f);
                    parcelObtain.writeFloat(f2);
                    parcelObtain.writeFloat(f3);
                    parcelObtain.writeFloat(f4);
                    if (clipData != null) {
                        parcelObtain.writeInt(1);
                        clipData.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(17, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void reportDropResult(IWindow iWindow, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(18, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void dragRecipientEntered(IWindow iWindow) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    this.mRemote.transact(19, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void dragRecipientExited(IWindow iWindow) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iWindow != null ? iWindow.asBinder() : null);
                    this.mRemote.transact(20, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void setWallpaperPosition(IBinder iBinder, float f, float f2, float f3, float f4) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeFloat(f);
                    parcelObtain.writeFloat(f2);
                    parcelObtain.writeFloat(f3);
                    parcelObtain.writeFloat(f4);
                    this.mRemote.transact(21, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void wallpaperOffsetsComplete(IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(22, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public Bundle sendWallpaperCommand(IBinder iBinder, String str, int i, int i2, int i3, Bundle bundle, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    int i4 = 1;
                    if (bundle != null) {
                        parcelObtain.writeInt(1);
                        bundle.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (!z) {
                        i4 = 0;
                    }
                    parcelObtain.writeInt(i4);
                    this.mRemote.transact(23, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? Bundle.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void wallpaperCommandComplete(IBinder iBinder, Bundle bundle) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    if (bundle != null) {
                        parcelObtain.writeInt(1);
                        bundle.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(24, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void setUniverseTransform(IBinder iBinder, float f, float f2, float f3, float f4, float f5, float f6, float f7) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeFloat(f);
                    parcelObtain.writeFloat(f2);
                    parcelObtain.writeFloat(f3);
                    parcelObtain.writeFloat(f4);
                    parcelObtain.writeFloat(f5);
                    parcelObtain.writeFloat(f6);
                    parcelObtain.writeFloat(f7);
                    this.mRemote.transact(25, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void onRectangleOnScreenRequested(IBinder iBinder, Rect rect, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    int i = 1;
                    if (rect != null) {
                        parcelObtain.writeInt(1);
                        rect.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (!z) {
                        i = 0;
                    }
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(26, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public IWindowId getWindowId(IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(27, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return IWindowId.Stub.asInterface(parcelObtain2.readStrongBinder());
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
