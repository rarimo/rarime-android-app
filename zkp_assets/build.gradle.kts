plugins {
    id("com.android.asset-pack")
}

assetPack {
    packName.set("zkp_assets") // Directory name for the asset pack
    dynamicDelivery {
        deliveryType = "install-time"
    }
}