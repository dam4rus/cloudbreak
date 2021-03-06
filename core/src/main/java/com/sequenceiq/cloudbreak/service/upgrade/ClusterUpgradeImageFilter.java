package com.sequenceiq.cloudbreak.service.upgrade;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.model.catalog.Image;
import com.sequenceiq.cloudbreak.cloud.model.catalog.Images;
import com.sequenceiq.cloudbreak.cloud.model.catalog.Versions;
import com.sequenceiq.cloudbreak.service.image.VersionBasedImageFilter;

@Component
public class ClusterUpgradeImageFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterUpgradeImageFilter.class);

    private static final String IGNORED_CM_VERSION = "7.x.0";

    private static final String CM_PACKAGE_KEY = "cm";

    private static final String STACK_PACKAGE_KEY = "stack";

    private static final String CFM_PACKAGE_KEY = "cfm";

    private static final String CSP_PACKAGE_KEY = "csp";

    private static final String SALT_PACKAGE_KEY = "salt";

    @Inject
    private VersionBasedImageFilter versionBasedImageFilter;

    @Inject
    private UpgradePermissionProvider upgradePermissionProvider;

    Images filter(List<Image> availableImages, Versions supportedVersions, Image currentImage, String cloudPlatform) {
        return new Images(null, null, null, getImages(availableImages, supportedVersions, currentImage, cloudPlatform), null);
    }

    private List<Image> getImages(List<Image> images, Versions versions, Image currentImage, String cloudPlatform) {
        List<Image> imagesForCbVersion = getImagesForCbVersion(versions, images);
        LOGGER.debug("{} image(s) found for the given CB version", imagesForCbVersion.size());
        return filterImages(imagesForCbVersion, currentImage, cloudPlatform);
    }

    private List<Image> getImagesForCbVersion(Versions supportedVersions, List<Image> availableImages) {
        return versionBasedImageFilter.getCdhImagesForCbVersion(supportedVersions, availableImages);
    }

    private List<Image> filterImages(List<Image> availableImages, Image currentImage, String cloudPlatform) {
        // I think the whole logic is broken, since we don't allow an upgrade if we shipped a new parcel extension...
        return availableImages.stream()
                .filter(ignoredCmVersion())
                .filter(validateCmVersion(currentImage).or(validateStackVersion(currentImage)))
                .filter(validateCloudPlatform(cloudPlatform))
                .filter(validateOsVersion(currentImage))
                // We don't necessary need CFM and CSP since only SDX upgrade is supported
                //.filter(validateCfmVersion(currentImage))
                //.filter(validateCspVersion(currentImage))
                .filter(validateSaltVersion(currentImage))
                .filter(filterCurrentImage(currentImage))
                .collect(Collectors.toList());
    }

    private Predicate<Image> validateOsVersion(Image currentImage) {
        return image -> isOsVersionsMatch(currentImage, image);
    }

    private boolean isOsVersionsMatch(Image currentImage, Image newImage) {
        return newImage.getOs().equalsIgnoreCase(currentImage.getOs())
                && newImage.getOsType().equalsIgnoreCase(currentImage.getOsType());
    }

    private Predicate<Image> ignoredCmVersion() {
        // There are some legacy CM versions that do not follow a proper versioning scheme, we must ignore them
        return image -> image.getPackageVersions().get(CM_PACKAGE_KEY) != null &&
                !image.getPackageVersions().get(CM_PACKAGE_KEY).contains(IGNORED_CM_VERSION);
    }

    private Predicate<Image> validateCmVersion(Image currentImage) {
        return image -> permitCmAndSatckUpgrade(currentImage, image, CM_PACKAGE_KEY);
    }

    private Predicate<Image> validateStackVersion(Image currentImage) {
        return image -> permitCmAndSatckUpgrade(currentImage, image, STACK_PACKAGE_KEY);
    }

    private Predicate<Image> validateCloudPlatform(String cloudPlatform) {
        return image -> image.getImageSetsByProvider().keySet().stream().anyMatch(key -> key.equalsIgnoreCase(cloudPlatform));
    }

    private Predicate<Image> validateCfmVersion(Image currentImage) {
        return image -> permitExtensionUpgrade(currentImage, image, CFM_PACKAGE_KEY);
    }

    private Predicate<Image> validateCspVersion(Image currentImage) {
        return image -> permitExtensionUpgrade(currentImage, image, CSP_PACKAGE_KEY);
    }

    private boolean permitCmAndSatckUpgrade(Image currentImage, Image image, String key) {
        return upgradePermissionProvider.permitCmAndSatckUpgrade(currentImage.getPackageVersions().get(STACK_PACKAGE_KEY),
                image.getPackageVersions().get(key));
    }

    private boolean permitExtensionUpgrade(Image currentImage, Image image, String key) {
        return upgradePermissionProvider.permitExtensionUpgrade(currentImage.getPackageVersions().get(STACK_PACKAGE_KEY),
                image.getPackageVersions().get(key));
    }

    private Predicate<Image> validateSaltVersion(Image currentImage) {
        return image -> image.getPackageVersions().get(SALT_PACKAGE_KEY).equals(currentImage.getPackageVersions().get(SALT_PACKAGE_KEY));
    }

    private Predicate<Image> filterCurrentImage(Image currentImage) {
        return image -> !image.getUuid().equals(currentImage.getUuid());
    }
}
