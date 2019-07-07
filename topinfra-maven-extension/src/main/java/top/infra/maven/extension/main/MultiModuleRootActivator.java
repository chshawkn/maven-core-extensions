package top.infra.maven.extension.main;

import static top.infra.maven.utils.MavenUtils.profileId;
import static top.infra.maven.utils.MavenUtils.projectName;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.profile.ProfileActivationContext;

import top.infra.maven.extension.activator.AbstractCustomActivator;
import top.infra.maven.extension.activator.model.ProjectBuilderActivatorModelResolver;
import top.infra.maven.extension.shared.MavenProjectInfo;
import top.infra.maven.extension.shared.MavenProjectInfoEventAware;

// @Component(role = CustomActivator.class, hint = "MultiModuleRootActivator")
@Named
@Singleton
public class MultiModuleRootActivator extends AbstractCustomActivator {

    private final MavenProjectInfoEventAware projectInfoBean;

    @Inject
    public MultiModuleRootActivator(
        final org.codehaus.plexus.logging.Logger logger,
        final ProjectBuilderActivatorModelResolver resolver,
        final MavenProjectInfoEventAware projectInfoBean
    ) {
        super(logger, resolver);

        this.projectInfoBean = projectInfoBean;
    }

    @Override
    protected String getName() {
        return "MultiModuleRootActivator";
    }

    @Override
    protected boolean isActive(
        final Model model,
        final Profile profile,
        final ProfileActivationContext context,
        final ModelProblemCollector problems
    ) {
        final boolean result;
        if (this.supported(profile)) {
            // if (logger.isDebugEnabled()) {
            //     logger.debug(String.format("%s project='%s' profile='%s' is multi_module_root_only profile",
            //         this.getName(), projectName(context), profileId(profile)));
            // }

            final MavenProjectInfo rootProjectInfo = this.projectInfoBean.getRootProjectInfo();

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("    %s rootProjectInfo: [%s], model: [%s]", this.getName(), rootProjectInfo, model));
            }

            result = rootProjectInfo != null && (rootProjectInfo.idEquals(model) || rootProjectInfo.idEqualsExceptInheritedGroupId(model));
        } else {
            result = false;

            if (logger.isDebugEnabled() || (profile.getId().contains("root") && profile.getId().contains("only"))) {
                logger.info(String.format("    %s project='%s' profile='%s' is not multi_module_root_only profile",
                    this.getName(), projectName(context), profileId(profile)));
            }
        }

        return result;
    }

    @Override
    public boolean supported(final Profile profile) {
        return profile.getId().contains("multi_module_root_only");
    }
}
