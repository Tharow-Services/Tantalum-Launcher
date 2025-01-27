

package net.tharow.tantalum.launcher.ui.components.news;

import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.launcher.ui.LauncherFrame;
import net.tharow.tantalum.launchercore.image.IImageJobListener;
import net.tharow.tantalum.launchercore.image.ImageJob;
import net.tharow.tantalum.platform.io.AuthorshipInfo;
import net.tharow.tantalum.utilslib.ImageUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.*;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class AuthorshipWidget extends JPanel implements IImageJobListener<AuthorshipInfo> {
    private JLabel avatarView;
    private final ResourceLoader resources;
    private JLabel authorName;
    private JLabel postTime;

    public AuthorshipWidget(ResourceLoader resources) {
        super();

        this.resources = resources;

        initComponents(resources);
    }

    public AuthorshipWidget(ResourceLoader resources, AuthorshipInfo authorshipInfo, ImageJob<AuthorshipInfo> avatar) {
        this(resources);
        setAuthorshipInfo(authorshipInfo, avatar);
    }

    private void initComponents(ResourceLoader resources) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setOpaque(false);

        avatarView = new JLabel();
        add(avatarView);

        add(Box.createHorizontalStrut(6));

        authorName = new JLabel("");
        authorName.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 12, Font.BOLD));
        authorName.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        add(authorName);

        add(Box.createHorizontalStrut(6));

        postTime = new JLabel("");
        postTime.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        postTime.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 12));
        add(postTime);

        add(Box.createHorizontalStrut(4));
    }

    private String getDateText(@NotNull Date date) {
        LocalDate posted = new LocalDate(date.getTime());
        LocalDate now = new LocalDate();

        Years yearsSince = Years.yearsBetween(posted, now);
        Months monthsSince = Months.monthsBetween(posted, now);
        Days daysSince = Days.daysBetween(posted, now);
        //Hours hoursSince = Hours.hoursBetween(posted, now);
        //Minutes minutesSince = Minutes.minutesBetween(posted, now);

        if (yearsSince.getYears() > 1)
            return resources.getString("time.years", Integer.toString(yearsSince.getYears()));
        else if (yearsSince.getYears() == 1)
            return resources.getString("time.year");
        else if (monthsSince.getMonths() > 1)
            return resources.getString("time.months", Integer.toString(monthsSince.getMonths()));
        else if (monthsSince.getMonths() == 1)
            return resources.getString("time.month");
        else if (daysSince.getDays() > 1)
            return resources.getString("time.days", Integer.toString(daysSince.getDays()));
        else if (daysSince.getDays() == 1)
            return resources.getString("time.day");
        //else if (hoursSince.getHours() > 1)
        //    return resources.getString("time.hours", Integer.toString(hoursSince.getHours()));
        //else if (hoursSince.getHours() == 1)
        //    return resources.getString("time.hour");
        //else if (minutesSince.getMinutes() > 1)
        //    return resources.getString("time.minutes", Integer.toString(minutesSince.getMinutes()));
        else
            return resources.getString("time.minute");
    }

    @Override
    public void jobComplete(ImageJob<AuthorshipInfo> job) {
        updateAvatar(job);
    }

    public void updateAvatar(ImageJob<AuthorshipInfo> job) {
        avatarView.setIcon(new ImageIcon(resources.getCircleClippedImage(ImageUtils.scaleWithAspectWidth(job.getImage(), 32))));
    }

    public void setAuthorshipInfo(AuthorshipInfo info, ImageJob<AuthorshipInfo> avatar) {
        postTime.setText(resources.getString("launcher.news.posted",getDateText(info.getDate()))+" ");
        authorName.setText(info.getUser());
        avatar.addJobListener(this);
        updateAvatar(avatar);
    }
}
